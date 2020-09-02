package top.hserver.core.event.queue;

import com.lmax.disruptor.EventTranslatorVararg;
import com.lmax.disruptor.RingBuffer;
import top.hserver.core.event.EventHandleMethod;

import java.lang.reflect.Method;


/**
 * @author hxm
 */
public class EventProducer {

    private final RingBuffer<EventData> ringBuffer;

    public EventProducer(RingBuffer<EventData> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(Object[] args) {
        long sequence = ringBuffer.next();
        try {
            EventData eventData = ringBuffer.get(sequence);
            eventData.setArgs(args);
        } finally {
            ringBuffer.publish(sequence);
        }
    }

}