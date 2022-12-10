package cn.hserver.core.server;

import cn.hserver.core.interfaces.ProtocolDispatcherAdapter;
import cn.hserver.core.server.context.ConstConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.hserver.core.interfaces.ServerCloseAdapter;
import cn.hserver.core.queue.QueueDispatcher;
import cn.hserver.core.interfaces.InitRunner;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.server.context.HumMessage;
import cn.hserver.core.server.handlers.HumClientHandler;
import cn.hserver.core.server.handlers.HumServerHandler;
import cn.hserver.core.server.util.*;
import cn.hserver.core.task.TaskManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.hserver.core.server.context.ConstConfig.*;

/**
 * @author hxm
 */

public class HServer extends AbsHServer {


    private static final Logger log = LoggerFactory.getLogger(HServer.class);

    private final Integer[] ports;

    private final String[] args;

    private final Map<Channel, String> channels = new HashMap<>();

    //UDP
    private EventLoopGroup humServerBossGroup = null;

    private EventLoopGroup humClientBossGroup = null;
    //TCP
    private EventLoopGroup bossGroup = null;
    private EventLoopGroup workerGroup = null;

    public HServer(Integer[] ports, String[] args) {
        this.ports = ports;
        this.args = args;
    }

    public void start() throws Exception {
        if (ConstConfig.HUM_OPEN) {
            //UDP Server
            humServerBossGroup = new NioEventLoopGroup();
            Bootstrap humServer = new Bootstrap();
            humServer.group(humServerBossGroup)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .handler(new HumServerHandler());
            Channel humChannel = humServer.bind(HUM_PORT).sync().channel();
            channels.put(humChannel, "UDP Server Port:" + HUM_PORT);
            //UDP Client
            humClientBossGroup = new NioEventLoopGroup();
            Bootstrap humClient = new Bootstrap();
            humClient.group(humClientBossGroup)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new HumClientHandler());
            HumClient.channel = humClient.bind(0).sync().channel();
            channels.put(HumClient.channel, "UDP Client Port:0");
        }

        List<ProtocolDispatcherAdapter> listBean = IocUtil.getListBean(ProtocolDispatcherAdapter.class);
        if (listBean != null && !listBean.isEmpty()) {
            //TCP Server
            String typeName;
            ServerBootstrap bootstrap = new ServerBootstrap();
            if (tcpOptions!=null){
                tcpOptions.forEach(bootstrap::option);
            }
            if (EpollUtil.check() && ConstConfig.EPOLL) {
                bootstrap.option(EpollChannelOption.SO_REUSEPORT, true);
                bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
                bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
                bossGroup = EventLoopUtil.getEventLoop(bossPool, "hserver_epoll_boss");
                workerGroup = EventLoopUtil.getEventLoop(workerPool, "hserver_epoll_worker");
                bootstrap.group(bossGroup, workerGroup).channel(EpollServerSocketChannel.class);
                typeName = "Epoll";
            } else {
                bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
                bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
                bossGroup = EventLoopUtil.getEventLoop(bossPool, "hserver_boss");
                workerGroup = EventLoopUtil.getEventLoop(workerPool, "hserver_worker");
                bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
                typeName = "Nio";
            }
            bootstrap.option(ChannelOption.SO_BACKLOG, backLog);
            bootstrap.childHandler(new ServerInitializer());
            StringBuilder portStr = new StringBuilder();
            for (Integer port : ports) {
                portStr.append(port).append(" ");
                Channel channel = bootstrap.bind(port).sync().channel();
                channels.put(channel, "TCP Server Port:" + port);
            }
            System.out.println();
            System.out.println(getHello(typeName, portStr.toString()));
            System.out.println();
        }
        log.info("HServer 启动完成");
        shutdownHook();
        initOk();
    }

    private void shutdownHook() {



        Thread shutdown = new NamedThreadFactory("hserver_shutdown").newThread(() -> {
            log.info("服务即将关闭");
            publishMessage(APP_NAME + "下线，IP：" + HServerIpUtil.getLocalIP());
            List<ServerCloseAdapter> listBean = IocUtil.getListBean(ServerCloseAdapter.class);
            if (listBean != null) {
                for (ServerCloseAdapter serverCloseAdapter : listBean) {
                    serverCloseAdapter.close();
                }
            }
            if (this.humClientBossGroup != null) {
                this.humClientBossGroup.shutdownGracefully();
            }
            if (this.humServerBossGroup != null) {
                this.humServerBossGroup.shutdownGracefully();
            }
            if (this.bossGroup != null) {
                this.bossGroup.shutdownGracefully();
            }
            if (this.workerGroup != null) {
                this.workerGroup.shutdownGracefully();
            }
            log.info("服务关闭完成");
        });
        Runtime.getRuntime().addShutdownHook(shutdown);
    }

    private void initOk() {
        //初始化完成可以放开任务了
        TaskManager.IS_OK = true;
        QueueDispatcher.startTaskThread();
        List<InitRunner> listBean = IocUtil.getListBean(InitRunner.class);
        if (listBean != null) {
            for (InitRunner initRunner : listBean) {
                initRunner.init(args);
            }
        }
    }


}
