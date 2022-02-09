package top.hserver.core.interfaces;

import ch.qos.logback.classic.spi.LoggingEvent;
import javassist.CtMethod;

/**
 * @author hxm
 */
public interface LogAdapter {

    /**
     * 当前被调用的的方法信息
     * @param loggingEvent
     * @throws Exception
     */
    void log(LoggingEvent loggingEvent) throws Exception;

}
