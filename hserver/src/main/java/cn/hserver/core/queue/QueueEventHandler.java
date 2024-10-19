package cn.hserver.core.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.server.util.ExceptionUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.BlockingQueue;

/**
 * @author hxm
 */

public class QueueEventHandler {
    private static final Logger log = LoggerFactory.getLogger(QueueEventHandler.class);

    private final Method method;
    private final Object obj;

    public QueueEventHandler(String queueName, Method method) {
        obj = IocUtil.getBean(queueName);
        this.method = method;
    }

    public void invoke(QueueData queueData) {
        try {
            Object[] args = queueData.getArgs();
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            method.invoke(obj, args);
        } catch (Exception e) {
            if (e instanceof InvocationTargetException) {
                log.error(ExceptionUtil.getMessage(((InvocationTargetException) e).getTargetException()));
            } else {
                log.error(e.getMessage(), e);
            }
        }
    }
}
