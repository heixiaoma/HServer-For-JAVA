package cn.hserver.core.server;

import cn.hserver.core.interfaces.LogAdapter;
import cn.hserver.core.interfaces.ProtocolDispatcherAdapter;
import cn.hserver.core.log.HServerLogAsyncAppender;
import cn.hserver.core.server.context.ConstConfig;
import cn.hserver.core.server.context.IoMultiplexer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.incubator.channel.uring.IOUringServerSocketChannel;
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

public class HServer {

    private static final Logger log = LoggerFactory.getLogger(HServer.class);

    private final Integer[] ports;

    private final Map<Channel, String> channels = new HashMap<>();

    //UDP
    private EventLoopGroup humServerBossGroup = null;

    private EventLoopGroup humClientBossGroup = null;
    //TCP
    private EventLoopGroup group = null;

    public HServer(Integer[] ports) {
        this.ports=ports;
    }

    public void run(Map<ChannelOption<Object>, Object> tcpOptions,Map<ChannelOption<Object>, Object> tcpChildOptions) throws Exception {
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
        if (listBean!=null&&!listBean.isEmpty()) {
            //TCP Server
            String typeName;
            ServerBootstrap bootstrap = new ServerBootstrap();
            if (tcpOptions!=null){
                tcpOptions.forEach(bootstrap::option);
            }
            if (tcpChildOptions!=null){
                tcpChildOptions.forEach(bootstrap::childOption);
            }
            IoMultiplexer eventLoopType = EventLoopUtil.getEventLoopType();
            if (eventLoopType!=IoMultiplexer.JDK) {
                bootstrap.option(EpollChannelOption.SO_REUSEPORT, true);
            }
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
            group = EventLoopUtil.getEventLoop(workerPool, "hserver_grop");
            bootstrap.group(group).channel(EventLoopUtil.getEventLoopTypeClass());
            typeName = eventLoopType.name();
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
    }

    private void shutdownHook() {

        publishMessage(APP_NAME + "上线，IP：" + HServerIpUtil.getLocalIP());

        channels.forEach((k, v) -> new NamedThreadFactory("hserver_close").newThread(() -> {
            try {
                k.closeFuture().sync();
                log.info("channel关闭,描述信息：{}", v);
            } catch (InterruptedException e) {
                log.error(e.getMessage(),e);
            }
        }).start());

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
            if (this.group != null) {
                this.group.shutdownGracefully();
            }
            log.info("服务关闭完成");
        });
        Runtime.getRuntime().addShutdownHook(shutdown);
    }


    private String getHello(String typeName, String port) {
        InputStream banner = HServer.class.getResourceAsStream("/banner.txt");
        try {
            if (banner != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(banner);
                String result = new BufferedReader(inputStreamReader)
                        .lines().collect(Collectors.joining(System.lineSeparator()));
                inputStreamReader.close();
                banner.close();
                return result;
            }
        } catch (IOException e) {
            log.error("banner.txt 流关闭错误{}", e.getMessage());
        }

        //GRAFFtit 字体
        return "  ___ ___  _________ \t运行方式：" + typeName + "\t端口：" + port + "\n" +
                " /   |   \\/   _____/ ______________  __ ___________ \n" +
                "/    ~    \\_____  \\_/ __ \\_  __ \\  \\/ // __ \\_  __ \\\n" +
                "\\    Y    /        \\  ___/|  | \\/\\   /\\  ___/|  | \\/\n" +
                " \\___|_  /_______  /\\___  >__|    \\_/  \\___  >__|   \n" +
                "       \\/        \\/     \\/                 \\/       ";
    }


    public void publishMessage(String message) {
        HumMessage humMessage = new HumMessage();
        humMessage.setData(message);
        humMessage.setType(SERVER_NAME);
        HumClient.sendMessage(humMessage);
    }

}
