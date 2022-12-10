package cn.hserver.core.server.util;

import cn.hserver.core.server.context.ConstConfig;
import io.netty.channel.epoll.Epoll;

/**
 * @author hxm
 */
public class EpollUtil {

  public static boolean check() {
    try {
      return Epoll.isAvailable() && System.getProperty("os.name").toLowerCase().contains("linux")&& ConstConfig.EPOLL;
    } catch (Exception e) {
      return false;
    }
  }

}
