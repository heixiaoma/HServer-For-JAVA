package top.hserver.core.server.handlers;

import io.netty.util.ReferenceCountUtil;
import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.context.HServerContext;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import top.hserver.core.server.util.ExceptionUtil;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


/**
 * @author hxm
 */
@Slf4j
public class ActionHandler extends SimpleChannelInboundHandler<HServerContext> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HServerContext hServerContext) throws Exception {
        try {
            CompletableFuture<HServerContext> future = CompletableFuture.completedFuture(hServerContext);
            Executor executor = ctx.executor();
            future.thenApplyAsync(req -> DispatcherHandler.staticFile(hServerContext), executor)
                    .thenApplyAsync(DispatcherHandler::staticFile, executor)
                    .thenApplyAsync(DispatcherHandler::permission, executor)
                    .thenApplyAsync(DispatcherHandler::filter, executor)
                    .thenApplyAsync(DispatcherHandler::findController, executor)
                    .thenApplyAsync(DispatcherHandler::buildResponse, executor)
                    .exceptionally(DispatcherHandler::handleException)
                    .thenAcceptAsync(msg -> DispatcherHandler.writeResponse(ctx, future, msg), ctx.channel().eventLoop());
        }finally {
            ReferenceCountUtil.release(hServerContext);
        }
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String message = ExceptionUtil.getMessage(cause);
        message="HServer:"+ ConstConfig.VERSION +"服务器异常:\n"+message;

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.SERVICE_UNAVAILABLE,
                Unpooled.wrappedBuffer(message.getBytes(StandardCharsets.UTF_8)));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        ctx.write(response);
        ctx.flush();
        ctx.close();
    }
}