package cn.hserver.core.logging;

import ch.qos.logback.core.PropertyDefinerBase;

public class HServerDataDefinerFile extends PropertyDefinerBase {

    @Override
    public String getPropertyValue() {
        String requestId = HServerPatternLayout.defaultConverterMap.get("requestId");
        if (requestId!=null){
            return "%d{yyyy-MM-dd HH:mm:ss.SSS} %5.5level --- [%17.17thread] %-40.40logger{40} [%4.4line] [%requestId] : %msg%n";
        }else {
            return "%d{yyyy-MM-dd HH:mm:ss.SSS} %5.5level --- [%17.17thread] %-40.40logger{40} [%4.4line] : %msg%n";
        }
    }
}
