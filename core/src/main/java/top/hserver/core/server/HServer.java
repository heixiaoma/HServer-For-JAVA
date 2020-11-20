package top.hserver.core.server;

import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.handler.ssl.SslContextBuilder;
import top.hserver.cloud.CloudManager;
import top.hserver.core.queue.QueueDispatcher;
import top.hserver.core.interfaces.InitRunner;
import top.hserver.core.ioc.IocUtil;
import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.util.NamedThreadFactory;
import top.hserver.core.server.util.EpollUtil;
import top.hserver.core.server.util.PropUtil;
import top.hserver.core.task.TaskManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

import static top.hserver.core.server.context.ConstConfig.*;

/**
 * @author hxm
 */
@Slf4j
public class HServer {

    private final int port;

    private final String[] args;

    public HServer(int port, String[] args) {
        this.port = port;
        this.args = args;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = null;
        EventLoopGroup workerGroup = null;

        String typeName;
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            if (EpollUtil.check() && EPOLL) {
                bootstrap.option(EpollChannelOption.SO_REUSEPORT, true);
                bossGroup = new EpollEventLoopGroup(bossPool, new NamedThreadFactory("hserver_epoll_boss"));
                workerGroup = new EpollEventLoopGroup(workerPool, new NamedThreadFactory("hserver_epoll_worker"));
                bootstrap.group(bossGroup, workerGroup).channel(EpollServerSocketChannel.class);
                typeName = "Epoll";
            } else {
                bossGroup = new NioEventLoopGroup(bossPool, new NamedThreadFactory("hserver_boss"));
                workerGroup = new NioEventLoopGroup(workerPool, new NamedThreadFactory("hserver_worker"));
                bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
                typeName = "Nio";
            }
            //看看有没有SSL
            initSSl();
            bootstrap.childHandler(new ServerInitializer());
            Channel ch = bootstrap.bind(port).sync().channel();
            log.info("HServer 启动完成");
            System.out.println();
            System.out.println(getHello(typeName, port));
            System.out.println();
            initOK();
            ch.closeFuture().sync();

        } finally {
            assert bossGroup != null;
            bossGroup.shutdownGracefully();
            assert workerGroup != null;
            workerGroup.shutdownGracefully();
        }
    }

    private void initOK() {
        //云启动
        CloudManager.run(port);
        //初始化完成可以放开任务了
        TaskManager.IS_OK = true;
        List<InitRunner> listBean = IocUtil.getListBean(InitRunner.class);
        if (listBean != null) {
            for (InitRunner initRunner : listBean) {
                initRunner.init(args);
            }
        }
        QueueDispatcher.startTaskThread();
    }


    private String getHello(String typeName, int port) {
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

    private void initSSl() {

        PropUtil instance = PropUtil.getInstance();
        String certFilePath = instance.get("certPath");
        String privateKeyPath = instance.get("privateKeyPath");
        String privateKeyPwd = instance.get("privateKeyPwd");
        if (privateKeyPath == null || certFilePath == null || privateKeyPath.trim().length() == 0 || certFilePath.trim().length() == 0) {
            return;
        }
        try {
            //检查下是不是外部路径。
            File cfile = new File(certFilePath);
            File pfile = new File(privateKeyPath);
            if (cfile.isFile() && pfile.isFile()) {
                ConstConfig.sslContext = SslContextBuilder.forServer(cfile, pfile, privateKeyPwd).build();
                return;
            }

            //看看是不是resources里面的
            InputStream cinput = HServer.class.getResourceAsStream("/ssl/" + certFilePath);
            InputStream pinput = HServer.class.getResourceAsStream("/ssl/" + privateKeyPath);

            if (cinput != null && pinput != null) {
                ConstConfig.sslContext = SslContextBuilder.forServer(cinput, pinput, privateKeyPwd).build();
                cinput.close();
                pinput.close();
            }
        } catch (Exception s) {
            log.error(s.getMessage());
            s.printStackTrace();
        }
    }

}
