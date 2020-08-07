package top.hserver.cloud.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.client.handler.RpcClientInitializer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hxm
 */
@Slf4j
public class RpcClient {

    public static Map<String, Channel> channels = new ConcurrentHashMap<>(1);


    public static void connect(String address) {
         String host = String.valueOf(address.split(":")[0]);
         int port = Integer.valueOf(address.split(":")[1]);
        try {
            final EventLoopGroup group = new NioEventLoopGroup();
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class);
            b.handler(new RpcClientInitializer());
            //发起异步连接请求，绑定连接端口和host信息
            final ChannelFuture future = b.connect(host, port).sync();

            future.addListener((ChannelFutureListener) arg0 -> {
                if (future.isSuccess()) {
                    log.debug("连接服务器成功");
                    channels.put(address, future.channel());
                } else {
                    log.debug("连接服务器失败");
                    future.cause().printStackTrace();
                    group.shutdownGracefully(); //关闭线程组
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
