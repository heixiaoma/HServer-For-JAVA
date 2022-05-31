package top.hserver.core.server.util;

import io.netty.channel.epoll.Epoll;

/**
 * @author hxm
 */
public class EpollUtil {

  public static boolean check() {
    try {
      return Epoll.isAvailable() && System.getProperty("os.name").toLowerCase().contains("linux");
    } catch (Exception e) {
      return false;
    }
  }

}
