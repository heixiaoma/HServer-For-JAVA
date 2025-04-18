package cn.hserver.plugin.web.context.sse;

import cn.hserver.plugin.web.context.HServerContextHolder;
import cn.hserver.plugin.web.context.HeadMap;
import cn.hserver.plugin.web.interfaces.HttpRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.internal.StringUtil;

import java.nio.charset.StandardCharsets;

public class SSeStream {

    private final HttpRequest request;
    private final HeadMap headMap;
    public SSeStream(Integer retryMilliseconds, HeadMap headMap) {
        this.request= HServerContextHolder.getWebKit().httpRequest;
        this.headMap = headMap;
        sendStartHeader();
        if (retryMilliseconds != null&&retryMilliseconds > 0) {
            sendRetryEvent(retryMilliseconds);
        }
    }

    public SSeStream sendSseEvent(SSeEvent sSeEvent){
        ChannelHandlerContext ctx = request.getCtx();
        String message;
        if (StringUtil.isNullOrEmpty(sSeEvent.getEvent())) {
            message = "data: " + sSeEvent.getData() + "\n\n";
        } else if (sSeEvent.getId()==null) {
            message = "event: " + sSeEvent.getEvent() + "\n" +
                    "data: " + sSeEvent.getData() + "\n\n";
        } else {
            message = "id: " + sSeEvent.getId() + "\n" +
                    "event: " + sSeEvent.getEvent() + "\n" +
                    "data: " + sSeEvent.getData() + "\n\n";
        }
        ctx.writeAndFlush(new DefaultHttpContent(Unpooled.wrappedBuffer(message.getBytes(StandardCharsets.UTF_8))));
        return this;
    }

    private void sendStartHeader(){
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        headMap.forEach((k,v)-> response.headers().add(k,v));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/event-stream");
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-cache");
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        ChannelHandlerContext ctx = request.getCtx();
        ctx.write(response);
    }

    private void sendRetryEvent(Integer retryMilliseconds) {
        ChannelHandlerContext ctx = request.getCtx();
        ByteBuf byteBuf = Unpooled.wrappedBuffer(("retry: " + retryMilliseconds + "\n\n").getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(new DefaultHttpContent(byteBuf));
    }


}
