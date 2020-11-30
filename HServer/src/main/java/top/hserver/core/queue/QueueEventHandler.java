package top.hserver.core.queue;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hserver.core.ioc.IocUtil;

import java.lang.reflect.Method;

/**
 * @author hxm
 */

public class QueueEventHandler implements EventHandler<QueueData>, WorkHandler<QueueData> {
    private static final Logger log = LoggerFactory.getLogger(QueueEventHandler.class);

    private String queueName;
    private Method method;


    public QueueEventHandler(String queueName, Method method) {
        this.queueName = queueName;
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
            method.invoke(IocUtil.getBean(queueName), args);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}