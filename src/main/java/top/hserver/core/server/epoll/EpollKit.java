package top.hserver.core.server.epoll;

import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import lombok.var;

public class EpollKit {

  public static NettyServerGroup group(int threadCount, int workers, String name) {
    var bossGroup = new EpollEventLoopGroup(threadCount, new NamedThreadFactory(name + "_epoll_boss@"));
    var workerGroup = new EpollEventLoopGroup(workers, new NamedThreadFactory(name + "_epoll_worker@"));
    return NettyServerGroup.builder().boosGroup(bossGroup).workerGroup(workerGroup).socketChannel(EpollServerSocketChannel.class).build();
  }

  public static boolean epollIsAvailable() {
    try {
      Object obj = Class.forName("io.netty.channel.epoll.Epoll").getMethod("isAvailable").invoke(null);
      return null != obj && Boolean.parseBoolean(obj.toString()) && System.getProperty("os.name").toLowerCase().contains("linux");
    } catch (Exception e) {
      return false;
    }
  }

}
