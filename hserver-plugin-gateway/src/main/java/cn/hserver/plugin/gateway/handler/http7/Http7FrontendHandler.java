package cn.hserver.plugin.gateway.handler.http7;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.ReferenceCountUtil;


public class Http7FrontendHandler extends ChannelInboundHandlerAdapter {

    private Channel outboundChannel;

    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void read(final ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            outboundChannel.writeAndFlush(msg);
        } else {
            closeOnFlush(ctx.channel());
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {

        if (outboundChannel == null) {
            HttpRequest httpRequest = (HttpRequest) msg;
            String host = httpRequest.headers().get("Host");


            final Channel inboundChannel = ctx.channel();

            Bootstrap b = new Bootstrap();
            b.group(ctx.channel().eventLoop());
            b.channel(NioSocketChannel.class).handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) {
                    ch.pipeline().addLast(new HttpClientCodec(),new ChunkedWriteHandler());
                    ch.pipeline().addLast(new Http7BackendHandler(inboundChannel));
                }
            });
            ChannelFuture f = b.connect("127.0.0.1", -1).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    future.channel().writeAndFlush(msg);
                } else {
                    future.channel().close();
                    ReferenceCountUtil.release(msg);
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
