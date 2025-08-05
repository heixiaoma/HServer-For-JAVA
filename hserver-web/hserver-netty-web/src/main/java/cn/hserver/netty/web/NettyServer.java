package cn.hserver.netty.web;

import cn.hserver.core.config.ConfigData;
import cn.hserver.mvc.server.WebServer;
import cn.hserver.netty.web.constants.IoMultiplexer;
import cn.hserver.netty.web.constants.NettyConfig;
import cn.hserver.netty.web.handler.NettyServerHandler;
import cn.hserver.netty.web.util.EventLoopUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class NettyServer implements WebServer {

    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);
    private final ServerBootstrap bootstrap = new ServerBootstrap();
   private  Channel channel;
   private  EventLoopGroup hserverGrop;


    @Override
    public void start(int port) {

        ConfigData instance = ConfigData.getInstance();
        NettyConfig.IO_MODE= IoMultiplexer.valueOf(instance.getString("netty.mode","IO_URING").toUpperCase(Locale.ROOT));
        NettyConfig.BACKLOG=instance.getInteger("netty.backlog",1024);
        NettyConfig.WORKER_POOL=instance.getInteger("netty.pool",0);
        NettyConfig.WRITE_LIMIT=instance.getLong("netty.write.limit");
        NettyConfig.READ_LIMIT=instance.getLong("netty.read.limit");
        NettyConfig.HTTP_CONTENT_SIZE=instance.getInteger("netty.content",Integer.MAX_VALUE);

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
        log.info("Netty事件处理模型:{}", EventLoopUtil.getEventLoopType());
    }

    @Override
    public void stop() {
        if (channel != null) {
            channel.close().syncUninterruptibly();
        }
        hserverGrop.shutdownGracefully().syncUninterruptibly();
    }
}
