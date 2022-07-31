package cn.hserver.plugin.gateway.handler.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

public class FrontendHandler extends ChannelInboundHandlerAdapter {

    private Channel outboundChannel;
    private String remoteHost = "";
    private int remotePort = 8888;


    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void read(final ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest httpRequest = (FullHttpRequest) msg;
            String hostPort = remoteHost + ":" + remotePort;
            httpRequest.headers().set(HttpHeaderNames.HOST, hostPort);
            httpRequest.headers().add("aa", "bb");
            outboundChannel.writeAndFlush(msg);
        } else {
            closeOnFlush(ctx.channel());
        }
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        if (outboundChannel == null) {
            final Channel inboundChannel = ctx.channel();

            Bootstrap b = new Bootstrap();
            b.group(inboundChannel.eventLoop());

            b.channel(NioSocketChannel.class).handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) {
                    ch.pipeline().addLast(new HttpClientCodec(), new HttpObjectAggregator(2000));
                    ch.pipeline().addLast(new BackendHandler(inboundChannel));
                }
            });

            ChannelFuture f = b.connect(remoteHost, remotePort).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    future.channel().writeAndFlush(msg);
                } else {
                    future.channel().close();
                }
            });
            outboundChannel = f.channel();
        } else {
            read(ctx, msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (outboundChannel != null) {
            closeOnFlush(outboundChannel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        closeOnFlush(ctx.channel());
    }
}