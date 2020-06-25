package top.hserver.core.properties;

import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.util.PropUtil;


/**
 * @author hxm
 */
public class PropertiesInit {

  public static void  init() {
    try {
      PropUtil propKit = new PropUtil();
      Object taskPool = propKit.get("taskPool");
      if (taskPool != null && taskPool.toString().trim().length() > 0) {
        ConstConfig.taskPool = Integer.parseInt(taskPool.toString());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
