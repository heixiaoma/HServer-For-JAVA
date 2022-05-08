package top.hserver.core.server.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hserver.core.server.context.HServerContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.hserver.core.server.util.ExceptionUtil;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


/**
 * @author hxm
 */
public class RouterHandler extends SimpleChannelInboundHandler<HServerContext> {
    private static final Logger log = LoggerFactory.getLogger(RouterHandler.class);

    private static Method getTtlExecutor = null;

    public RouterHandler(ChannelHandlerContext ctx) {
        try {
            if (getTtlExecutor == null) {
                getTtlExecutor = ctx.executor().getClass().getMethod("getTtlExecutor");
                getTtlExecutor.setAccessible(true);
            }
        } catch (Exception e) {
            log.error(ExceptionUtil.getMessage(e));
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HServerContext hServerContext) throws Exception {
        CompletableFuture<HServerContext> future = CompletableFuture.completedFuture(hServerContext);
        Executor executor = (Executor) getTtlExecutor.invoke(ctx.executor());
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