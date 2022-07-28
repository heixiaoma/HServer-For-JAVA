package cn.hserver.plugin.web.handlers;

import cn.hserver.core.server.HServer;
import cn.hserver.plugin.web.context.WebConstConfig;
import com.alibaba.ttl.threadpool.TtlExecutors;
import cn.hserver.plugin.web.context.HServerContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;


/**
 * @author hxm
 */
public class RouterHandler extends SimpleChannelInboundHandler<HServerContext> {
    private static Executor executor;

    public RouterHandler() {
        if (executor == null) {
            if (WebConstConfig.BUSINESS_EVENT != null) {
                executor = TtlExecutors.getTtlExecutor(WebConstConfig.BUSINESS_EVENT);
            } else {
                executor = TtlExecutors.getTtlExecutor(HServer.workerGroup);
            }
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HServerContext hServerContext) throws Exception {
        CompletableFuture<HServerContext> future = CompletableFuture.completedFuture(hServerContext);
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
