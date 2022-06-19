package net.hserver.plugin.rpc.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import net.hserver.plugin.rpc.codec.Msg;
import net.hserver.plugin.rpc.codec.MsgType;
import net.hserver.plugin.rpc.codec.ResultData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ClientHandler extends SimpleChannelInboundHandler<Msg> {
    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);

    private NettyChannel nettyChannel;

    public ClientHandler(NettyChannel nettyChannel) {
        this.nettyChannel = nettyChannel;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Msg msg) throws Exception {
        if (msg.getMsgType() == MsgType.RESULT) {
            ResultData data = (ResultData) msg.getData();
            CompletableFuture data1 = (CompletableFuture) data.getData();
            CompletableFuture completableFuture = RpcClient.mapping.get(data.getRequestId());
            RpcClient.mapping.remove(data.getRequestId());
            completableFuture.complete(data1.get());
        }
    }

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
//                log.debug("读空闲");
            } else if (event.state() == IdleState.WRITER_IDLE) {
                ctx.channel().writeAndFlush(new Msg<>(MsgType.HEART));
                log.debug("写空闲，发送心跳");
            } else if (event.state() == IdleState.ALL_IDLE) {
                log.debug("读写空闲，发送心跳");
                ctx.channel().writeAndFlush(new Msg<>(MsgType.HEART));
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        final EventLoop loop = ctx.channel().eventLoop();
        loop.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    nettyChannel.connect();
                    log.warn("channelInactive 断开链接重连 成功 {}", ctx.channel().localAddress().toString());
                } catch (Exception e) {
                    log.error("channelInactive 断开链接重连失败");
                }
            }
        }, 1L, TimeUnit.SECONDS);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("断开异常信息：\r\n{}", cause.getMessage());
        ctx.close();
    }

}
