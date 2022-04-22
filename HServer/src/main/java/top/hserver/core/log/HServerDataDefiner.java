package top.hserver.core.log;

import ch.qos.logback.core.PropertyDefinerBase;
import top.hserver.core.server.context.ConstConfig;

public class HServerDataDefiner extends PropertyDefinerBase {

    @Override
    public String getPropertyValue() {
        Boolean runjar = ConstConfig.RUNJAR;
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        if (runjar && isWindows) {
            //非高亮格式
            return "%d{yyyy-MM-dd HH:mm:ss.SSS} %5.5level --- [%17.17thread] %-40.40logger{40} [%4.4line] [%requestId] : %msg%n";
        } else {
            //高亮格式
            return "%d{yyyy-MM-dd HH:mm:ss.SSS} %HServerHighlight(%5.5level) 线程名: [%17.17thread] %cyan(%-40.40logger{39} 行号:[%4.4line]) 请求ID： [%requestId] : %msg%n";
        }
    }
}