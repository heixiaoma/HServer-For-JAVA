package top.hserver.core.queue;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import lombok.extern.slf4j.Slf4j;
import top.hserver.core.ioc.IocUtil;

import java.lang.reflect.Method;
/**
 * @author hxm
 */
@Slf4j
public class QueueEventHandler implements EventHandler<QueueData>, WorkHandler<QueueData> {

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