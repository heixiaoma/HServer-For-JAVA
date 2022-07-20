package cn.hserver.plugin.web.handlers;

import cn.hserver.core.server.handlers.DispatcherHandler;
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
    private static final Map<Integer, Executor> cache = new ConcurrentHashMap<>();
    @Override
    public void channelRead0(ChannelHandlerContext ctx, HServerContext hServerContext) throws Exception {
        CompletableFuture<HServerContext> future = CompletableFuture.completedFuture(hServerContext);
        int i = ctx.executor().hashCode();
        Executor executor = cache.get(i);
        if (executor == null) {
            executor = TtlExecutors.getTtlExecutor(ctx.executor());
            cache.put(i, executor);
        }
        future.thenApplyAsync(req -> cn.hserver.core.server.handlers.DispatcherHandler.staticFile(hServerContext), executor)
                .thenApplyAsync(cn.hserver.core.server.handlers.DispatcherHandler::permission, executor)
                .thenApplyAsync(cn.hserver.core.server.handlers.DispatcherHandler::filter, executor)
                .thenApplyAsync(cn.hserver.core.server.handlers.DispatcherHandler::findController, executor)
                .thenApplyAsync(cn.hserver.core.server.handlers.DispatcherHandler::buildResponse, executor)
                .exceptionally(cn.hserver.core.server.handlers.DispatcherHandler::handleException)
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
