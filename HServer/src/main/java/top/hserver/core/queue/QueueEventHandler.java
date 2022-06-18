package top.hserver.core.queue;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hserver.core.ioc.IocUtil;
import top.hserver.core.server.util.ExceptionUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author hxm
 */

public class QueueEventHandler implements EventHandler<QueueData>, WorkHandler<QueueData> {
    private static final Logger log = LoggerFactory.getLogger(QueueEventHandler.class);

    private final Method method;

    public QueueEventHandler(Method method) {
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
            method.setAccessible(true);
            method.invoke(IocUtil.getBean(queueData.getQueueName()), args);
        } catch (Exception e) {
            if (e instanceof InvocationTargetException) {
                log.error(ExceptionUtil.getMessage(((InvocationTargetException)e).getTargetException()));
            } else {
                log.error(ExceptionUtil.getMessage(e));
            }
        }finally {
           QueueDispatcher.removeKey(queueData.getId(),queueData.getQueueName());
        }
    }
}