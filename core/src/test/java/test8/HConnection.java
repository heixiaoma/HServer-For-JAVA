package test8;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import top.hserver.core.server.util.NamedThreadFactory;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class HConnection {

    public static int POOL_SIZE = 4;
    public final static ScheduledExecutorService POOL = Executors.newScheduledThreadPool(POOL_SIZE, new NamedThreadFactory("HClient-CallBack"));

    private ChannelFuture channelFuture;
    private NioEventLoopGroup workerGroup;
    private Bootstrap b;

    public HConnection(String host, int port) {
        try {
            b = new Bootstrap();
            workerGroup = new NioEventLoopGroup(new NamedThreadFactory("HClient"));
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new HttpClientCodec());
                    ch.pipeline().addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
                    ch.pipeline().addLast(new HttpClientInboundHandler());
                }
            });
            channelFuture = b.connect(host, port).sync().addListener(re -> {
                if (!re.isSuccess()) {
                    re.cause().printStackTrace();
                }
            });
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


    public HFuture write(DefaultFullHttpRequest request, HResponse.Listener listener) {
        HFuture future = null;
        if (this.channelFuture.channel().isActive()) {
            if (listener != null) {
                future = new HFuture(listener);
            } else {
                future = new HFuture();
            }
            future.setId(UUID.randomUUID().toString());
            ChannelManager.setAttr(this.channelFuture.channel(), future);
            this.channelFuture.channel().writeAndFlush(request);
            System.out.println("发送成功");
        } else {
            System.out.println("离线了");
        }
        return future;
    }
}
