package cn.hserver.core.log;

import ch.qos.logback.core.PropertyDefinerBase;
import cn.hserver.core.server.context.ConstConfig;

public class HServerDataDefinerFile extends PropertyDefinerBase {

    @Override
    public String getPropertyValue() {
        return "%d{yyyy-MM-dd HH:mm:ss.SSS} %5.5level --- [%17.17thread] %-40.40logger{40} [%4.4line] [%requestId] : %msg%n";
    }
}
