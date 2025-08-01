package cn.hserver.netty.web.handler.http;

import cn.hserver.mvc.sse.SSeStream;
import cn.hserver.netty.web.context.HttpResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class NettSse extends SSeStream {
    private static final Logger log = LoggerFactory.getLogger(SSeStream.class);
    private final Channel channel;
    private final HttpResponse response;
    public NettSse(Integer retryMilliseconds, HttpResponse response) {
        super(retryMilliseconds);
        this.channel=response.getCtx().channel();
        this.response=response;
    }

    @Override
    public SSeStream sendSseEvent(String event) {
             channel.writeAndFlush(new DefaultHttpContent(Unpooled.wrappedBuffer(event.getBytes(StandardCharsets.UTF_8)))).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("sendSseEvent error", future.cause());
            }else {
                log.debug(event.trim());
            }
        });
        return this;
    }

    @Override
    public void sendStartHeader() {
        DefaultHttpResponse defaultHttpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.getHeaders().forEach((k, v) -> defaultHttpResponse.headers().add(k, v));
        defaultHttpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/event-stream");
        defaultHttpResponse.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-cache");
        defaultHttpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        channel.writeAndFlush(defaultHttpResponse).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("sendSseEvent error", future.cause());
            }
        });
    }

    @Override
    public SSeStream addCloseListener(Runnable runnable) {
        channel.closeFuture().addListener(future -> {
            runnable.run();
        });
        return this;
    }

    @Override
    public void sendRetryEvent(String event) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(event.getBytes(StandardCharsets.UTF_8));
        channel.writeAndFlush(new DefaultHttpContent(byteBuf)).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("sendSseEvent error", future.cause());
            }
        });
    }
}
