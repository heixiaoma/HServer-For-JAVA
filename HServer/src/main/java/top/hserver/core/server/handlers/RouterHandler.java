package top.hserver.core.server.handlers;

import com.alibaba.ttl.threadpool.TtlExecutors;
import top.hserver.core.server.context.HServerContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;


/**
 * @author hxm
 */
public class RouterHandler extends SimpleChannelInboundHandler<HServerContext> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HServerContext hServerContext) throws Exception {
        CompletableFuture<HServerContext> future = CompletableFuture.completedFuture(hServerContext);
        ExecutorService executor = TtlExecutors.getTtlExecutorService(ctx.executor());
        future.thenApplyAsync(req -> DispatcherHandler.staticFile(hServerContext), executor)
                .thenApplyAsync(DispatcherHandler::permission, executor)
                .thenApplyAsync(DispatcherHandler::filter, executor)
                .thenApplyAsync(DispatcherHandler::findController, executor)
                .thenApplyAsync(DispatcherHandler::buildResponse, executor)
                .exceptionally(DispatcherHandler::handleException)
                .thenAcceptAsync(msg -> DispatcherHandler.writeResponse(ctx, future, msg), executor);

    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        BuildResponse.writeException(ctx, cause);
    }
}