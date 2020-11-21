package test8;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.net.SocketAddress;
import java.net.URI;

public class HConnection {

    private ChannelFuture channelFuture;
    private NioEventLoopGroup workerGroup;
    private Bootstrap b;

    public HConnection(String host, int port, int pool) {
        try {
            b = new Bootstrap();
            workerGroup = new NioEventLoopGroup(pool);
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
            channelFuture = b.connect(host, port).sync();
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

    public void write(FullHttpRequest request) {
        this.channelFuture.channel().pipeline().write(request);
    }
}
