package top.hserver.core.server.handlers;

import io.netty.util.ReferenceCountUtil;
import top.hserver.core.server.context.HServerContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

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
                    .thenApplyAsync(DispatcherHandler::permission, executor)
                    .thenApplyAsync(DispatcherHandler::filter, executor)
                    .thenApplyAsync(DispatcherHandler::findController, executor)
                    .thenApplyAsync(DispatcherHandler::buildResponse, executor)
                    .exceptionally(DispatcherHandler::handleException)
                    .thenAcceptAsync(msg -> DispatcherHandler.writeResponse(ctx, future, msg),executor);
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
      BuildResponse.writeException(ctx,cause);
    }
}