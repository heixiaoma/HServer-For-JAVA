package cn.hserver.netty.web;

import cn.hserver.core.config.ConfigData;
import cn.hserver.core.ioc.annotation.Component;
import cn.hserver.mvc.server.SslData;
import cn.hserver.mvc.server.WebServer;
import cn.hserver.netty.web.constants.IoMultiplexer;
import cn.hserver.netty.web.constants.NettyConfig;
import cn.hserver.netty.web.handler.NettyServerHandler;
import cn.hserver.netty.web.util.EventLoopUtil;
import cn.hserver.netty.web.util.SslContextUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class NettyServer implements WebServer {

    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);
    private final ServerBootstrap bootstrap = new ServerBootstrap();
    private final List<Channel> channels = new ArrayList<>();
    private EventLoopGroup hserverGrop;


    @Override
    public void start(int port, int sslPort, SslData sslData) {
        ConfigData instance = ConfigData.getInstance();
        NettyConfig.IO_MODE = IoMultiplexer.valueOf(instance.getString("netty.mode", "IO_URING").toUpperCase(Locale.ROOT));
        NettyConfig.BACKLOG = instance.getInteger("netty.backlog", 1024);
        NettyConfig.WORKER_POOL = instance.getInteger("netty.pool", 0);
        NettyConfig.WRITE_LIMIT = instance.getLong("netty.write.limit");
        NettyConfig.READ_LIMIT = instance.getLong("netty.read.limit");
        NettyConfig.HTTP_CONTENT_SIZE = instance.getInteger("netty.content", Integer.MAX_VALUE);

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
            if (port > 0) {
                channels.add(bootstrap.bind(port).sync().channel());
                log.info("Netty http server start port:{}", port);
            }
            if (SslContextUtil.initSsl(sslPort, sslData)) {
                channels.add(bootstrap.bind(sslPort).sync().channel());
                log.info("Netty https server start port:{}", sslPort);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("Netty事件处理模型:{}", EventLoopUtil.getEventLoopType());
    }

    @Override
    public void stop() {
        for (Channel channel : channels) {
            channel.close().syncUninterruptibly();
        }
        hserverGrop.shutdownGracefully().syncUninterruptibly();
    }
}
