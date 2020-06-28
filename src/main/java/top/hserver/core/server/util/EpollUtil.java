package top.hserver.core.server.util;

/**
 * @author hxm
 */
public class EpollUtil {

  public static boolean check() {
    try {
      Object obj = Class.forName("io.netty.channel.epoll.Epoll").getMethod("isAvailable").invoke(null);
      return null != obj && Boolean.parseBoolean(obj.toString()) && System.getProperty("os.name").toLowerCase().contains("linux");
    } catch (Exception e) {
      return false;
    }
  }

}
