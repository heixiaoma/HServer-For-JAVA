package top.hserver.core.event.queue;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import top.hserver.core.event.EventHandleMethod;
import top.hserver.core.server.util.NamedThreadFactory;

import java.util.LinkedList;

public class QueueFactoryImpl implements QueueFactory {

    private Disruptor<EventHandleMethod> disruptor;

    @Override
    public void createQueue(String queueName, int bufferSize,LinkedList<EventHandleMethod> eventHandleMethods) {
        // 创建disruptor

        disruptor = new Disruptor<>(EventHandleMethod::new, bufferSize, new NamedThreadFactory("queue:" + queueName));
        //多消费者重复消费
        disruptor.handleEventsWith(new QueueEventHandler("A"), new QueueEventHandler("B"));
        //多消费者不重复消费
        disruptor.handleEventsWithWorkerPool(new QueueEventHandler("C"), new QueueEventHandler("D")).thenHandleEventsWithWorkerPool(new QueueEventHandler("E"));
        // 启动
        disruptor.start();
    }

    @Override
    public void producer(EventHandleMethod event) {
        RingBuffer<EventHandleMethod> ringBuffer = disruptor.getRingBuffer();
        EventProducer producer = new EventProducer(ringBuffer);
        producer.onData(event);
    }

}