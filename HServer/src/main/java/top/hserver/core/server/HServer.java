package top.hserver.core.server;

import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.handler.ssl.*;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hserver.core.interfaces.ServerCloseAdapter;
import top.hserver.core.queue.QueueDispatcher;
import top.hserver.core.interfaces.InitRunner;
import top.hserver.core.ioc.IocUtil;
import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.util.NamedThreadFactory;
import top.hserver.core.server.util.EpollUtil;
import top.hserver.core.server.util.PropUtil;
import top.hserver.core.server.util.SslContextUtil;
import top.hserver.core.task.TaskManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

import static io.netty.handler.codec.http2.Http2SecurityUtil.CIPHERS;
import static top.hserver.core.server.context.ConstConfig.*;

/**
 * @author hxm
 */

public class HServer {

    private static final Logger log = LoggerFactory.getLogger(HServer.class);

    private final Integer[] ports;

    private final String[] args;

    private EventLoopGroup bossGroup = null;

    private EventLoopGroup workerGroup = null;

    public HServer(Integer[] port, String[] args) {
        String portsStr = PropUtil.getInstance().get("ports");
        if (portsStr.trim().length() > 0) {
            String[] portStrs = portsStr.split(",");
            ports = new Integer[portStrs.length];
            for (int i = 0; i < portStrs.length; i++) {
                ports[i] = Integer.parseInt(portStrs[i]);
            }
        } else {
            this.ports = port;
        }
        this.args = args;
    }

    public void run() throws Exception {
        String typeName;
        ServerBootstrap bootstrap = new ServerBootstrap();
        if (EpollUtil.check() && EPOLL) {
            bootstrap.option(EpollChannelOption.SO_REUSEPORT, true);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
            bossGroup = new EpollEventLoopGroup(bossPool, new NamedThreadFactory("hserver_epoll_boss"));
            workerGroup = new EpollEventLoopGroup(workerPool, new NamedThreadFactory("hserver_epoll_worker"));
            bootstrap.group(bossGroup, workerGroup).channel(EpollServerSocketChannel.class);
            typeName = "Epoll";
        } else {
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
            bossGroup = new NioEventLoopGroup(bossPool, new NamedThreadFactory("hserver_boss"));
            workerGroup = new NioEventLoopGroup(workerPool, new NamedThreadFactory("hserver_worker"));
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
            typeName = "Nio";
        }
        //看看有没有SSL
        SslContextUtil.setSsl();
        bootstrap.childHandler(new ServerInitializer());
        String portStr = "";
        for (Integer port : ports) {
            portStr = portStr + (port + " ");
            bootstrap.bind(port).sync().channel();
        }
        log.info("HServer 启动完成");
        System.out.println();

        System.out.println(getHello(typeName, portStr));
        System.out.println();
        initOk();
        shutdownHook();
    }

    private void shutdownHook() {
        Thread shutdown = new NamedThreadFactory("hserver_shutdown").newThread(() -> {
            log.info("服务即将关闭");
            List<ServerCloseAdapter> listBean = IocUtil.getListBean(ServerCloseAdapter.class);
            if (listBean != null) {
                for (ServerCloseAdapter serverCloseAdapter : listBean) {
                    serverCloseAdapter.close();
                }
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

}
