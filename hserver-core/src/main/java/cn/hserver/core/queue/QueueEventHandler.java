package cn.hserver.core.queue;

import cn.hserver.core.context.IocApplicationContext;
import cn.hserver.core.queue.bean.QueueData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author hxm
 */

public class QueueEventHandler {
    private static final Logger log = LoggerFactory.getLogger(QueueEventHandler.class);

    private final Method method;
    private final String handlerName;

    private Object handler;


    public QueueEventHandler(String handlerName, Method method) {
        this.method = method;
        this.handlerName = handlerName;
    }

    public void invoke(QueueData queueData) {
        try {
            if (handler == null) {
                handler = IocApplicationContext.getBean(handlerName);
            }
            Object[] args = queueData.getArgs();
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            method.invoke(handler, args);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
