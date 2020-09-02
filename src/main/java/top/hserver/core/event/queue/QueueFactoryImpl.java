package top.hserver.core.event.queue;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.util.DaemonThreadFactory;
import top.hserver.core.event.EventHandleMethod;
import top.hserver.core.ioc.annotation.event.EventHandlerType;
import top.hserver.core.server.util.NamedThreadFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hxm
 */
public class QueueFactoryImpl implements QueueFactory {

    private Disruptor<EventData> disruptor;

    @Override
    public void createQueue(String queueName, int bufferSize, EventHandlerType eventHandlerType, List<EventHandleMethod> eventHandleMethods) {
        // 创建disruptor
        disruptor = new Disruptor<>(EventData::new, bufferSize, new NamedThreadFactory("h-q:" + queueName));

        Map<Integer, List<EventHandleMethod>> collect = eventHandleMethods.stream().sorted(Comparator.comparingInt(EventHandleMethod::getLevel)).collect(Collectors.groupingBy(EventHandleMethod::getLevel));

        EventHandlerGroup<EventData> eventHandlerGroup = null;

        Iterator<Integer> iterator = collect.keySet().iterator();
        int flag = 0;
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            List<EventHandleMethod> handleMethods = collect.get(next);
            //检查哈是否有那种设置了多个消费者的添加进去
            for (int i = 0; i < handleMethods.size(); i++) {
                EventHandleMethod eventHandleMethod = handleMethods.get(i);
                int size = eventHandleMethod.getSize();
                if (size > 1) {
                    for (int j = 0; j < size - 1; j++) {
                        handleMethods.add(eventHandleMethod);
                    }
                    eventHandleMethod.setSize(1);
                }
            }
            if (flag == 0) {
                QueueEventHandler[] queueEventHandlers = new QueueEventHandler[handleMethods.size()];
                for (int i = 0; i < handleMethods.size(); i++) {
                    EventHandleMethod eventHandleMethod = handleMethods.get(i);
                    queueEventHandlers[i] = new QueueEventHandler(queueName, eventHandleMethod.getMethod());
                }
                //多消费者重复消费
                if (eventHandlerType == EventHandlerType.REPEAT_CONSUMPTION) {
                    eventHandlerGroup = disruptor.handleEventsWith(queueEventHandlers);
                } else {
                    //多消费者不重复消费
                    eventHandlerGroup = disruptor.handleEventsWithWorkerPool(queueEventHandlers);
                }
                flag++;
            } else {
                QueueEventHandler[] queueEventHandlers = new QueueEventHandler[handleMethods.size()];
                for (int i = 0; i < handleMethods.size(); i++) {
                    EventHandleMethod eventHandleMethod = handleMethods.get(i);
                    queueEventHandlers[i] = new QueueEventHandler(queueName, eventHandleMethod.getMethod());
                }
                //多消费者重复消费
                if (eventHandlerType == EventHandlerType.REPEAT_CONSUMPTION) {
                    eventHandlerGroup.then(queueEventHandlers);
                } else {
                    //多消费者不重复消费
                    eventHandlerGroup.thenHandleEventsWithWorkerPool(queueEventHandlers);
                }
            }
        }
        disruptor.start();
    }

    @Override
    public void producer(Object[] args) {
        RingBuffer<EventData> ringBuffer = disruptor.getRingBuffer();
        EventProducer producer = new EventProducer(ringBuffer);
        producer.onData(args);
    }

}