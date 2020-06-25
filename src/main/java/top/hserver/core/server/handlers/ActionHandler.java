package top.hserver.core.server.handlers;

import top.hserver.core.server.context.HServerContext;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


@Slf4j
public class ActionHandler extends SimpleChannelInboundHandler<HServerContext> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HServerContext hServerContext) throws Exception {
        CompletableFuture<HServerContext> future = CompletableFuture.completedFuture(hServerContext);
        Executor executor = ctx.executor();
        future.thenApplyAsync(req -> DispatcherHandler.buildHServerContext(ctx, hServerContext), executor)
                .thenApplyAsync(DispatcherHandler::Statistics, executor)
                .thenApplyAsync(DispatcherHandler::staticFile, executor)
                .thenApplyAsync(DispatcherHandler::Permission, executor)
                .thenApplyAsync(DispatcherHandler::filter, executor)
                .thenApplyAsync(DispatcherHandler::findController, executor)
                .thenApplyAsync(DispatcherHandler::buildResponse, executor)
                .exceptionally(DispatcherHandler::handleException)
                .thenAcceptAsync(msg -> DispatcherHandler.writeResponse(ctx, future, msg), ctx.channel().eventLoop());
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.SERVICE_UNAVAILABLE,
                Unpooled.wrappedBuffer("服务器为检查到的错误".getBytes(StandardCharsets.UTF_8)));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        ctx.write(response);
        ctx.flush();
        ctx.close();
    }
}