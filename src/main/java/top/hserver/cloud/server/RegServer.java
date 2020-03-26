package top.hserver.cloud.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.server.handler.RpcServerInitializer;
import top.hserver.core.server.epoll.EpollKit;
import top.hserver.core.server.epoll.NamedThreadFactory;
import top.hserver.core.server.epoll.NettyServerGroup;

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
        int acceptThreadCount = 1;
        int ioThreadCount = 0;
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            if (EpollKit.epollIsAvailable()) {
                bootstrap.option(EpollChannelOption.SO_REUSEPORT, true);
                NettyServerGroup nettyServerGroup = EpollKit.group(acceptThreadCount, ioThreadCount, "hserver_rpc");
                bossGroup = nettyServerGroup.getBoosGroup();
                workerGroup = nettyServerGroup.getWorkerGroup();
                bootstrap.group(bossGroup, workerGroup).channel(nettyServerGroup.getSocketChannel());
            } else {
                bossGroup = new NioEventLoopGroup(acceptThreadCount, new NamedThreadFactory("hserver_rpc_boss@"));
                workerGroup = new NioEventLoopGroup(ioThreadCount, new NamedThreadFactory("hserver_rpc_ worker@"));
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
