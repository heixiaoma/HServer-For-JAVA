package top.hserver.core.event.queue;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import lombok.extern.slf4j.Slf4j;
import top.hserver.core.event.EventHandleMethod;
import top.hserver.core.ioc.IocUtil;

import java.lang.reflect.Method;

/**
 * @author hxm
 */
@Slf4j
public class QueueEventHandler implements EventHandler<EventData>, WorkHandler<EventData> {

    private String queueName;
    private Method method;

    public QueueEventHandler(String queueName, Method method) {
        this.queueName = queueName;
        this.method = method;
    }

    @Override
    public void onEvent(EventData event, long sequence, boolean endOfBatch) throws Exception {
        invoke(event);
    }

    @Override
    public void onEvent(EventData event) throws Exception {
        invoke(event);
    }

    private void invoke(EventData eventHandleMethod) {
        Object[] args = eventHandleMethod.getArgs();
        try {
            method.invoke(IocUtil.getBean(queueName), args);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }
}