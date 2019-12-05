package com.hserver.core.server;

/**
 * Created by Bess on 23.09.14.
 */

import com.hserver.core.server.epoll.EpollKit;
import com.hserver.core.server.epoll.NamedThreadFactory;
import com.hserver.core.server.epoll.NettyServerGroup;
import com.hserver.core.task.TaskManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HServer {

    private final int port;

    public HServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        int acceptThreadCount = 1;
        int ioThreadCount = 0;
        String typeName;
        System.out.println();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            if (EpollKit.epollIsAvailable()) {
                bootstrap.option(EpollChannelOption.SO_REUSEPORT, true);
                NettyServerGroup nettyServerGroup = EpollKit.group(acceptThreadCount, ioThreadCount);
                bossGroup = nettyServerGroup.getBoosGroup();
                workerGroup = nettyServerGroup.getWorkerGroup();
                bootstrap.group(bossGroup, workerGroup).channel(nettyServerGroup.getSocketChannel());
                typeName = "Epoll";
            } else {
                bossGroup = new NioEventLoopGroup(acceptThreadCount, new NamedThreadFactory("接收器@"));
                workerGroup = new NioEventLoopGroup(ioThreadCount, new NamedThreadFactory("工作器@"));
                bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
                typeName = "Nio";
            }
            bootstrap.childHandler(new HttpNettyServerInitializer());
            Channel ch = bootstrap.bind(port).sync().channel();
            log.info("HServer 启动完成");
            System.out.println(getHello(typeName, port));
            System.out.println();
            //初始化完成可以放开任务了
            TaskManager.IS_OK = true;
            ch.closeFuture().sync();

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    public String getHello(String typeName, int port) {

        return "  ___ ___  _________ \t方式运行：" + typeName + "\t端口：" + port + "\n" +
                " /   |   \\/   _____/ ______________  __ ___________ \n" +
                "/    ~    \\_____  \\_/ __ \\_  __ \\  \\/ // __ \\_  __ \\\n" +
                "\\    Y    /        \\  ___/|  | \\/\\   /\\  ___/|  | \\/\n" +
                " \\___|_  /_______  /\\___  >__|    \\_/  \\___  >__|   \n" +
                "       \\/        \\/     \\/                 \\/       ";
    }

}
