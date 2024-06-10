package cn.hserver.plugin.web.handlers;

import cn.hserver.plugin.web.context.HServerContext;
import cn.hserver.plugin.web.handlers.check.*;
import com.alibaba.ttl.threadpool.TtlExecutors;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;


/**
 * @author hxm
 */
@ChannelHandler.Sharable
public class RouterHandler extends SimpleChannelInboundHandler<HServerContext> {

    private static final RouterHandler instance = new RouterHandler();
    private static final boolean useCtxExecutor = false;

    private RouterHandler() {}

    public static RouterHandler getInstance() {
        return instance;
    }

    private final DispatcherHandler limit = new Limit();
    private final DispatcherHandler staticFile = new StaticFile();
    private final DispatcherHandler filter = new Filter();
    private final DispatcherHandler permission = new Permission();
    private final DispatcherHandler findController = new FindController();

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HServerContext hServerContext) throws Exception {
        CompletableFuture<HServerContext> future = CompletableFuture.completedFuture(hServerContext);
        Executor executor;
        if (useCtxExecutor) {
            executor = ctx.executor();
        } else {
            executor = TtlExecutors.getTtlExecutor(ctx.executor());
        }
        future.thenApplyAsync(req -> limit.dispatcher(hServerContext), executor)
                .thenApplyAsync(staticFile::dispatcher, executor)
                .thenApplyAsync(filter::dispatcher, executor)
                .thenApplyAsync(permission::dispatcher, executor)
                .thenApplyAsync(findController::dispatcher, executor)
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
