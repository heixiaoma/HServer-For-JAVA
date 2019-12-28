package top.hserver.cloud.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import top.hserver.cloud.server.handler.FileServerInitializer;

public class ChatServer {

    private final int port;

    public ChatServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        int acceptThreadCount = 1;
        int ioThreadCount = 0;
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bossGroup = new NioEventLoopGroup(acceptThreadCount, new NioEventLoopGroup());
            workerGroup = new NioEventLoopGroup(ioThreadCount, new NioEventLoopGroup());
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new FileServerInitializer());
            Channel ch = bootstrap.bind(port).sync().channel();
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
