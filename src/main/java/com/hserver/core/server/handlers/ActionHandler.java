package com.hserver.core.server.handlers;

import com.hserver.core.ioc.IocUtil;
import com.hserver.core.server.WebContext;
import com.hserver.core.server.exception.BusinessException;
import com.hserver.core.server.router.RequestType;
import com.hserver.core.server.router.RouterInfo;
import com.hserver.core.server.router.RouterManager;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * for hello request
 * Created by Bess on 23.09.14.
 */

@Slf4j
public class ActionHandler extends SimpleChannelInboundHandler<HttpRequest> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpRequest httpRequest) throws Exception {

        CompletableFuture<HttpRequest> future = CompletableFuture.completedFuture(httpRequest);

        Executor executor = ctx.executor();

        future.thenApplyAsync(req -> Dispatcher.buildWebContext(ctx, req), executor)
                .thenApplyAsync(Dispatcher::Statistics, executor)
                .thenApplyAsync(Dispatcher::findController, executor)
                .thenApplyAsync(Dispatcher::buildResponse, executor)
                .exceptionally(Dispatcher::handleException)
                .thenAcceptAsync(msg -> Dispatcher.writeResponse(ctx, future, msg), ctx.channel().eventLoop());
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof BusinessException) {
            BusinessException businessException = (BusinessException) cause;
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(businessException.getRespMsg().getBytes(Charset.forName("UTF-8"))));
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            ctx.write(response);
            ctx.flush();
        }
        ctx.close();
    }
}