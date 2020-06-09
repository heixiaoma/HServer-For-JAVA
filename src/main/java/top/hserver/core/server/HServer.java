package top.hserver.core.server;

/**
 * Created by Bess on 23.09.14.
 */

import io.netty.handler.ssl.SslContextBuilder;
import top.hserver.core.interfaces.InitRunner;
import top.hserver.core.ioc.IocUtil;
import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.epoll.EpollKit;
import top.hserver.core.server.epoll.NamedThreadFactory;
import top.hserver.core.server.epoll.NettyServerGroup;
import top.hserver.core.server.util.PropUtil;
import top.hserver.core.task.TaskManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLException;
import java.io.File;
import java.io.InputStream;

import static top.hserver.core.event.EventDispatcher.startTaskThread;

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

    int acceptThreadCount = 2;
    int ioThreadCount = 4;
    String typeName;
    try {
      ServerBootstrap bootstrap = new ServerBootstrap();
      if (EpollKit.epollIsAvailable()) {
        bootstrap.option(EpollChannelOption.SO_REUSEPORT, true);
        NettyServerGroup nettyServerGroup = EpollKit.group(acceptThreadCount, ioThreadCount, "hserver");
        bossGroup = nettyServerGroup.getBoosGroup();
        workerGroup = nettyServerGroup.getWorkerGroup();
        bootstrap.group(bossGroup, workerGroup).channel(nettyServerGroup.getSocketChannel());
        typeName = "Epoll";
      } else {
        bossGroup = new NioEventLoopGroup(acceptThreadCount, new NamedThreadFactory("hserver_boss@"));
        workerGroup = new NioEventLoopGroup(ioThreadCount, new NamedThreadFactory("hserver_ worker@"));
        bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
        typeName = "Nio";
      }


      //看看有没有SSL
      initSSl();
      bootstrap.childHandler(new HttpNettyServerInitializer());
      Channel ch = bootstrap.bind(port).sync().channel();
      log.info("HServer 启动完成");
      System.out.println();
      System.out.println(getHello(typeName, port));
      System.out.println();
      initOK();
      ch.closeFuture().sync();

    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }

  private void initOK() {
    //初始化完成可以放开任务了
    TaskManager.IS_OK = true;
    InitRunner bean = IocUtil.getBean(InitRunner.class);
    if (bean != null) {
      bean.init(args);
    }
    startTaskThread();
  }


  private String getHello(String typeName, int port) {

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
    if (privateKeyPath == null || certFilePath == null ||privateKeyPath.trim().length()==0||certFilePath.trim().length()==0) {
      return;
    }
    try {
      //检查下是不是外部路径。
      File cfile = new File(certFilePath);
      File pfile = new File(privateKeyPath);
      if (cfile.isFile() && pfile.isFile()) {
        ConstConfig.sslContext = SslContextBuilder.forServer(cfile, pfile,privateKeyPwd).build();
        return;
      }

      //看看是不是resources里面的
      InputStream cinput = HServer.class.getResourceAsStream("/ssl/"+certFilePath);
      InputStream pinput = HServer.class.getResourceAsStream("/ssl/"+privateKeyPath);

      if (cinput != null && pinput != null) {
        ConstConfig.sslContext = SslContextBuilder.forServer(cinput, pinput,privateKeyPwd).build();
        return;
      }
    }catch (SSLException s){
      log.error(s.getMessage());
      s.printStackTrace();
    }
  }

}
