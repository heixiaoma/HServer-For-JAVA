package top.hserver.cloud.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.client.handler.HChannelPoolHandler;
import top.hserver.core.server.util.NamedThreadFactory;

import java.net.InetSocketAddress;

/**
 * @author hxm
 */
@Slf4j
public class RpcClient {

    public static ChannelPoolMap<InetSocketAddress, SimpleChannelPool> channels;

    public static void init() {
        try {
            final EventLoopGroup group = new NioEventLoopGroup(new NamedThreadFactory("Rpc-Client"));
            final Bootstrap strap = new Bootstrap();
            strap.group(group).channel(NioSocketChannel.class);
            channels = new AbstractChannelPoolMap<InetSocketAddress, SimpleChannelPool>() {
                @Override
                protected SimpleChannelPool newPool(InetSocketAddress key) {
                    return new FixedChannelPool(strap.remoteAddress(key), new HChannelPoolHandler(), 2);
                }
            };
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
