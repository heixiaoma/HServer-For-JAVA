package test8;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import top.hserver.core.server.util.NamedThreadFactory;

public class HConnection {

    private ChannelFuture channelFuture;
    private NioEventLoopGroup workerGroup;
    private Bootstrap b;

    public HConnection(String host, int port, int pool) {
        try {
            b = new Bootstrap();
            workerGroup = new NioEventLoopGroup(new NamedThreadFactory("HClient"));
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    // 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
                    ch.pipeline().addLast(new HttpResponseDecoder());
                    // 客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码
                    ch.pipeline().addLast(new HttpRequestEncoder());
                    ch.pipeline().addLast(new HttpClientInboundHandler());
                }
            });
            channelFuture = b.connect(host, port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean isActive() {
        if (channelFuture != null) {
            if (channelFuture.channel().isActive()) {
                return true;
            } else {
                channelFuture.channel().close();
                workerGroup.shutdownGracefully();
            }
        }
        return false;
    }

    public void stop() {
        if (channelFuture != null) {
            channelFuture.channel().close();
            workerGroup.shutdownGracefully();
        }
    }

    public void write(DefaultFullHttpRequest request) {
        try {
            this.channelFuture.channel().pipeline().writeAndFlush(request);
        } finally {
            ReferenceCountUtil.release(request.content());
        }
    }
}
