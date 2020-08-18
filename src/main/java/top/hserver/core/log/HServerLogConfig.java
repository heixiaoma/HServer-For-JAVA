package top.hserver.core.log;

import org.apache.logging.log4j.core.LoggerContext;
import java.net.URI;

/**
 * @author hxm
 */
public class HServerLogConfig {

  private String[] getStandardConfigLocations() {
    return new String[]{"logback-test.groovy", "logback-test.xml", "logback.groovy", "logback.xml"};
  }

  public void init() {
    try {
      if (existConfig()){
        return;
      }
      System.setProperty("log4j.skipJansi","false");
      LoggerContext loggerContext = (LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
      loggerContext.setConfigLocation(URI.create("log4j2-hserver.xml"));
      loggerContext.reconfigure();
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


}
