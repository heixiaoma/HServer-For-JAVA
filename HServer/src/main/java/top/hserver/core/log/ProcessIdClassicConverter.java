package top.hserver.core.log;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * @author hxm
 */
public class ProcessIdClassicConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {
        return ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    }

}
