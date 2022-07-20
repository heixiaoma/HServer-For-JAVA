package cn.hserver.plugin.web.log;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import cn.hserver.plugin.web.context.HServerContextHolder;
import cn.hserver.plugin.web.context.Webkit;

/**
 * @author hxm
 */
public class RequestIdClassicConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {
        Webkit webKit = HServerContextHolder.getWebKit();
        if (webKit != null) {
            return webKit.httpRequest.getRequestId();
        }
        return "system-id";
    }

}
