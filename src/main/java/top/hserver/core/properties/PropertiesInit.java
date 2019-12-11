package top.hserver.core.properties;

import top.hserver.core.server.context.ConstConfig;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesInit {

    public void init() {
        try {
            Properties pps = new Properties();
            InputStream resourceAsStream = PropertiesInit.class.getResourceAsStream("/application.properties");
            pps.load(resourceAsStream);
            //开启匹配
            Object statistics = pps.get("statistics");
            if (statistics != null) {
                ConstConfig.isStatisticsOpen = Boolean.valueOf(statistics.toString());
            }
            //匹配规则
            String statisticalRules = pps.getProperty("statisticalRules");
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
