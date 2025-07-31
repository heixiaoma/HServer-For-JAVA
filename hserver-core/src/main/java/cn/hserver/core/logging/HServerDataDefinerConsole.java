package cn.hserver.core.logging;


import ch.qos.logback.core.PropertyDefinerBase;
import cn.hserver.core.config.ConstConfig;

public class HServerDataDefinerConsole extends PropertyDefinerBase {

    @Override
    public String getPropertyValue() {
        Boolean runjar = ConstConfig.RUN_JAR;
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        //web框架会提供这个请求ID
        String requestId = HServerPatternLayout.defaultConverterMap.get("requestId");
        if (runjar && isWindows) {
            //非高亮格式
            if (requestId!=null) {
                return "%d{yyyy-MM-dd HH:mm:ss.SSS} %5.5level --- [%17.17thread] %-40.40logger{40} [%4.4line] [%requestId] : %msg%n";
            }else {
                return "%d{yyyy-MM-dd HH:mm:ss.SSS} %5.5level --- [%17.17thread] %-40.40logger{40} [%4.4line] : %msg%n";
            }
        } else {
            if (requestId!=null) {
                //高亮格式
                return "%d{yyyy-MM-dd HH:mm:ss.SSS} %HServerHighlight(%5.5level) 线程名: [%17.17thread] %cyan(%-40.40logger{39} 行号:[%4.4line]) 请求ID： [%requestId] : %msg%n";
            }else {
                return "%d{yyyy-MM-dd HH:mm:ss.SSS} %HServerHighlight(%5.5level) 线程名: [%17.17thread] %cyan(%-40.40logger{39} 行号:[%4.4line]) : %msg%n";
            }
        }
    }
}
