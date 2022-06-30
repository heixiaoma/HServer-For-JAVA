package cn.hserver.plugin.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.hserver.core.server.util.NamedThreadFactory;


/**
 * netty 客户端连接对象类
 */
public class NettyChannel {

    private static final Logger log = LoggerFactory.getLogger(NettyChannel.class);

    private String host;
    private int port;

    NettyChannel(String host, int port) {
        try {
            this.host = host;
            this.port = port;
            connect();
        } catch (Exception e) {
        }
    }

    private Channel ch;

    public Channel getCh() {
        return ch;
    }

    public void connect() throws Exception {
        final EventLoopGroup group = new NioEventLoopGroup(new NamedThreadFactory("Rpc-Client"));
        final Bootstrap strap = new Bootstrap();
        strap.group(group).channel(NioSocketChannel.class).handler(new ClientHandlersInitializer(this));
        ch = strap.connect(host, port).sync().channel();
    }
}