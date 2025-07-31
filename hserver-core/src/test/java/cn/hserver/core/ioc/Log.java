package cn.hserver.core.ioc;

import ch.qos.logback.classic.spi.LoggingEvent;
import cn.hserver.core.ioc.annotation.Component;
import cn.hserver.core.logging.LogAdapter;

@Component
public class Log implements LogAdapter {
  @Override
  public void log(LoggingEvent loggingEvent) {
      System.out.println(loggingEvent.getMessage());
  }
}