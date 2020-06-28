package top.hserver.cloud.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.server.handler.RpcServerInitializer;
import top.hserver.core.server.util.NamedThreadFactory;
import top.hserver.core.server.util.EpollUtil;
@Slf4j
public class RegServer extends Thread {

    private final int port;

    public RegServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup = null;
        EventLoopGroup workerGroup = null;
        int bossGroupThreadCount = 1;
        int workerGroupThreadCount = 0;
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            if (EpollUtil.check()) {
              bootstrap.option(EpollChannelOption.SO_REUSEPORT, true);
              bossGroup = new EpollEventLoopGroup(bossGroupThreadCount, new NamedThreadFactory("hserver_epoll_rpc_boss"));
              workerGroup = new EpollEventLoopGroup(workerGroupThreadCount, new NamedThreadFactory("hserver_epoll_rpc_worker"));
              bootstrap.group(bossGroup, workerGroup).channel(EpollServerSocketChannel.class);
            } else {
                bossGroup = new NioEventLoopGroup(bossGroupThreadCount, new NamedThreadFactory("hserver_rpc_boss"));
                workerGroup = new NioEventLoopGroup(workerGroupThreadCount, new NamedThreadFactory("hserver_rpc_ worker"));
                bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
            }
            bootstrap.childHandler(new RpcServerInitializer());
            Channel ch = bootstrap.bind(port).sync().channel();
            ch.closeFuture().sync();
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
