package com.hserver.core.server.handlers;

import com.hserver.core.server.context.WebContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObjectHandler extends SimpleChannelInboundHandler<HttpObject> {
    private WebContext webContext;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject msg) throws Exception {
        if (msg instanceof io.netty.handler.codec.http.HttpRequest) {
            webContext = new WebContext();
            webContext.setHttpRequest((io.netty.handler.codec.http.HttpRequest) msg);
            return;
        }
        if (null != webContext && msg instanceof HttpContent) {
            webContext.appendContent((HttpContent) msg);
        }
        if (msg instanceof LastHttpContent) {
            if (null != webContext) {
                channelHandlerContext.fireChannelRead(webContext);
            } else {
                channelHandlerContext.fireChannelRead(msg);
            }
        }
    }
}