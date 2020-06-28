package top.hserver.core.log;

import ch.qos.logback.classic.LoggerContext;
import org.slf4j.impl.StaticLoggerBinder;

import java.io.InputStream;

/**
 * @author hxm
 */
public class HServerLogConfig {

  private LoggerContext factory;

  private String[] getStandardConfigLocations() {
    return new String[]{"logback-test.groovy", "logback-test.xml", "logback.groovy", "logback.xml"};
  }

  public void init() {
    try {
      if (existConfig()){
        return;
      }
      factory = (LoggerContext) StaticLoggerBinder.getSingleton().getLoggerFactory();
      loadConfiguration(HServerLogConfig.class.getResourceAsStream("/logback-hserver.xml"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private boolean existConfig(){
    for (String s : getStandardConfigLocations()) {
      if (HServerLogConfig.class.getResourceAsStream("/"+s)!=null){
      return true;
      }
    }
    return false;
  }

  private void stopAndReset(LoggerContext loggerContext) {
    loggerContext.stop();
    loggerContext.reset();
  }

  private void loadConfiguration(InputStream in) throws Exception {
    LoggerContext loggerContext = this.factory;
    stopAndReset(loggerContext);
    ch.qos.logback.classic.joran.JoranConfigurator configurator = new ch.qos.logback.classic.joran.JoranConfigurator();
    configurator.setContext(loggerContext);
    configurator.doConfigure(in);
  }

}
