package cn.hserver.core.server;


import cn.hserver.core.interfaces.InitRunner;
import cn.hserver.core.interfaces.ServerCloseAdapter;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.queue.QueueDispatcher;
import cn.hserver.core.server.context.ConstConfig;
import cn.hserver.core.server.context.HumMessage;
import cn.hserver.core.server.util.*;
import cn.hserver.core.task.TaskManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.hserver.core.server.context.ConstConfig.APP_NAME;

import static cn.hserver.core.server.context.ConstConfig.SERVER_NAME;

public class HNetty {

    private static final Logger log = LoggerFactory.getLogger(HNetty.class);

    private final Map<Channel, String> channels = new HashMap<>();

    private final List<EventLoopGroup> ALl_LOOP = new ArrayList<>();

    public HNetty(ServerType serverType, Integer[] ports, Map<ChannelOption<Object>, Object> option, Map<ChannelOption<Object>, Object> childOption) throws InterruptedException {
        switch (serverType) {
            case TCP: {
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                option.forEach(serverBootstrap::option);
                childOption.forEach(serverBootstrap::childOption);
                EventLoopGroup serverBoss = EventLoopUtil.getEventLoop(ConstConfig.bossPool, "server_boss");
                EventLoopGroup serverWorker = EventLoopUtil.getEventLoop(ConstConfig.workerPool, "server_worker");
                ALl_LOOP.add(serverBoss);
                ALl_LOOP.add(serverWorker);
                serverBootstrap.group(serverBoss, serverWorker);
                if (EpollUtil.check()) {
                    serverBootstrap.channel(EpollServerSocketChannel.class);
                } else {
                    serverBootstrap.channel(NioServerSocketChannel.class);
                }
                serverBootstrap.childHandler(new ServerInitializer());
                for (Integer port : ports) {
                    Channel channel = serverBootstrap.bind(port).sync().channel();
                    channels.put(channel, "TCP Server Port:" + port);
                }
            }
            case UDP: {
                Bootstrap bootstrap = new Bootstrap();
                option.forEach(bootstrap::option);
                EventLoopGroup serverUdp = EventLoopUtil.getEventLoop(ConstConfig.bossPool, "server_udp");
                ALl_LOOP.add(serverUdp);
                bootstrap.group(serverUdp);
                if (EpollUtil.check()) {
                    bootstrap.channel(EpollServerSocketChannel.class);
                } else {
                    bootstrap.channel(NioDatagramChannel.class);
                }
                for (Integer port : ports) {
                    Channel channel = bootstrap.bind(port).sync().channel();
                    channels.put(channel, "UDP Server Port:" + port);
                }
            }
        }

        StringBuilder portStr = new StringBuilder();
        for (Integer port : ports) {
            portStr.append(port).append(" ");
        }
        shutdownHook(serverType.name() + ":" + portStr);
        log.info("服务器启动完成");
        System.out.println();
        System.out.println(getHello(getHello(String.valueOf(portStr)));
        System.out.println();
    }

    public Map<Channel, String> getChannels() {
        return this.channels;
    }

    private void shutdownHook(String name) {
        publishMessage(APP_NAME + "上线，IP：" + HServerIpUtil.getLocalIP());
        channels.forEach((k, v) -> new NamedThreadFactory("hserver_close").newThread(() -> {
            try {
                k.closeFuture().sync();
                log.info("channel关闭,描述信息：{}", v);
            } catch (InterruptedException e) {
                log.error(ExceptionUtil.getMessage(e));
            }
        }).start());
        Thread shutdown = new NamedThreadFactory("server_shutdown-" + name).newThread(() -> {
            publishMessage(APP_NAME + "下线，IP：" + HServerIpUtil.getLocalIP());
            List<ServerCloseAdapter> listBean = IocUtil.getListBean(ServerCloseAdapter.class);
            if (listBean != null) {
                for (ServerCloseAdapter serverCloseAdapter : listBean) {
                    serverCloseAdapter.close();
                }
            }
            for (EventLoopGroup eventExecutors : ALl_LOOP) {
                eventExecutors.shutdownGracefully();
            }
        });
        Runtime.getRuntime().addShutdownHook(shutdown);
    }


    private String getHello(String port) {
        String typeName = EpollUtil.check() ? "Epoll" : "Nio";
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

    private void publishMessage(String message) {
        HumMessage humMessage = new HumMessage();
        humMessage.setData(message);
        humMessage.setType(SERVER_NAME);
        HumClient.sendMessage(humMessage);
    }


}
