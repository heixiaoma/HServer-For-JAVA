package cn.hserver.core.queue;

import cn.hserver.core.queue.fqueue.FQueue;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.server.util.ExceptionUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

/**
 * @author hxm
 */

public class QueueEventHandler implements EventHandler<QueueData>, WorkHandler<QueueData> {
    private static final Logger log = LoggerFactory.getLogger(QueueEventHandler.class);

    private final Method method;
    private final Object obj;

    public QueueEventHandler(String queueName, Method method) {
        obj = IocUtil.getBean(queueName);
        this.method = method;
    }

    @Override
    public void onEvent(QueueData event, long sequence, boolean endOfBatch) throws Exception {
        invoke(event);
    }

    @Override
    public void onEvent(QueueData event) throws Exception {
        invoke(event);
    }

    private void invoke(QueueData queueData) {
        Object[] args = queueData.getArgs();
        try {
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
        } finally {
            if (queueData.getThreadSize() == 1) {
                queueData.getfQueue().poll();
            }
        }
    }
}
