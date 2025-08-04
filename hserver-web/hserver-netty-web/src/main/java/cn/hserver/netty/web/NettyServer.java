package cn.hserver.netty.web;

import cn.hserver.mvc.server.WebServer;
import cn.hserver.netty.web.constants.NettyConfig;
import cn.hserver.netty.web.handler.NettyServerHandler;
import cn.hserver.netty.web.util.EventLoopUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;

public class NettyServer implements WebServer {

   private final ServerBootstrap bootstrap = new ServerBootstrap();
   private  Channel channel;
   private  EventLoopGroup hserverGrop;


    @Override
    public void start(int port) {
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        hserverGrop = EventLoopUtil.getEventLoop(NettyConfig.WORKER_POOL, "hserver_grop");
        bootstrap.group(hserverGrop).channel(EventLoopUtil.getEventLoopTypeClass());
        bootstrap.option(ChannelOption.SO_BACKLOG, NettyConfig.BACKLOG);
        switch (EventLoopUtil.getEventLoopType()) {
            case EPOLL:
            case IO_URING:
                bootstrap.option(EpollChannelOption.SO_REUSEPORT, true);
                break;
        }
        bootstrap.childHandler(new NettyServerHandler());
        try {
            channel = bootstrap.bind(port).sync().channel();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        if (channel != null) {
            channel.close().syncUninterruptibly();
        }
        hserverGrop.shutdownGracefully().syncUninterruptibly();
    }
}
