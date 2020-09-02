package top.hserver.core.event.queue;

import com.lmax.disruptor.EventTranslatorVararg;
import com.lmax.disruptor.RingBuffer;
import top.hserver.core.event.EventHandleMethod;

import java.lang.reflect.Method;


public class EventProducer {

    private final RingBuffer<EventHandleMethod> ringBuffer;

    public EventProducer(RingBuffer<EventHandleMethod> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    private static final EventTranslatorVararg<EventHandleMethod> TRANSLATOR = (event, sequence, args) -> {
        event.setClassName((String) args[0]);
        event.setMethod((Method) args[1]);
        event.setArgs(args[2]);
        event.setLevel((Integer) args[3]);
    };

    public void onData(EventHandleMethod event) {
        ringBuffer.publishEvent(TRANSLATOR, event.getClassName(), event.getMethod(), event.getArgs(), event.getLevel());
    }

}