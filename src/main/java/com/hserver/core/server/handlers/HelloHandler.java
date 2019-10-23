package com.hserver.core.server.handlers;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LANGUAGE;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;

/**
 * for hello request
 * Created by Bess on 23.09.14.
 */
public class HelloHandler extends SimpleChannelInboundHandler<HttpRequest> {
    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpRequest req) throws Exception {
        if (req.getUri().equals("/hello") || req.getUri().equals("/hello/")) {
            FullHttpResponse response =
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                            Unpooled.wrappedBuffer("OK".getBytes()));
            response.headers()
                    .set(CONTENT_TYPE, "text/html")
                    .add(CONTENT_LANGUAGE, response.content().readableBytes())
                    .add(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            ctx.write(response);
            ctx.flush();
        } else {
            ctx.fireChannelRead(req);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}