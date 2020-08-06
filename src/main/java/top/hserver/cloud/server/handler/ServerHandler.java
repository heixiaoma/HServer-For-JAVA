package top.hserver.cloud.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.common.Msg;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<Msg> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Msg msg) throws Exception {
      CompletableFuture<Msg> futures = CompletableFuture.completedFuture(msg);
      Executor executor = channelHandlerContext.executor();
        futures.thenApplyAsync(req -> InvokerHandler.buildContext(channelHandlerContext, msg), executor)
                .thenApplyAsync(InvokerHandler::invoker, executor)
                .exceptionally(InvokerHandler::handleException)
                .thenAcceptAsync(message -> InvokerHandler.writeResponse(channelHandlerContext, futures, message),executor);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
