package top.hserver.core.properties;

import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.util.PropUtil;


public class PropertiesInit {

  public static void  init() {
    try {
      PropUtil propKit = new PropUtil();
      Object taskPool = propKit.get("taskPool");
      if (taskPool != null && taskPool.toString().trim().length() > 0) {
        ConstConfig.taskPool = Integer.parseInt(taskPool.toString());
      }
      //开启匹配
      Object statistics = propKit.get("statistics");
      if (statistics != null) {
        ConstConfig.isStatisticsOpen = Boolean.valueOf(statistics.toString());
      }
      //匹配规则
      String statisticalRules = propKit.get("statisticalRules");
      if (statisticalRules != null && statisticalRules.trim().length() > 0) {
        String[] split = statisticalRules.split(",");
        for (String s : split) {
          if (statisticalRules.trim().length() > 0) {
            ConstConfig.StatisticalRules.add(s);
          }
        }
      } else {
        ConstConfig.StatisticalRules.add("/.*");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
