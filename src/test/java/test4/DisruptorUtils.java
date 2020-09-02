package test4;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

public class DisruptorUtils {

    private static Disruptor<Event> disruptor;

    static {
        int bufferSize = 1024;
        // 创建disruptor
        disruptor = new Disruptor<>(Event::new, bufferSize, DaemonThreadFactory.INSTANCE);
        //多消费者重复消费
        disruptor.handleEventsWith(new SpikeEventHandler1(),new SpikeEventHandler2());
        //多消费者不重复消费
        disruptor.handleEventsWithWorkerPool(new SpikeEventHandler1(),new SpikeEventHandler2());
        // 启动
        disruptor.start();
    }

    public static void producer(Object ...data){
        RingBuffer<Event> ringBuffer = disruptor.getRingBuffer();
        EventProducer producer = new EventProducer(ringBuffer);
        producer.onData(data);
    }

}