package top.hserver.cloud.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.bean.*;
import top.hserver.cloud.client.handler.InvokerHandler;
import top.hserver.cloud.common.Msg;
import top.hserver.cloud.future.SyncWrite;
import top.hserver.cloud.future.SyncWriteFuture;
import top.hserver.cloud.future.SyncWriteMap;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<Msg> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Msg msg) throws Exception {
      CompletableFuture<Msg> futures = CompletableFuture.completedFuture(msg);
      Executor executor = channelHandlerContext.executor();
      futures.thenApplyAsync(e-> RpcServerHandler.readData(channelHandlerContext, msg), executor);
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
