package test4;

import com.lmax.disruptor.EventTranslatorVararg;
import com.lmax.disruptor.RingBuffer;


public class EventProducer {

    private final RingBuffer<Event> ringBuffer;

    public EventProducer(RingBuffer<Event> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    private static final EventTranslatorVararg<Event> TRANSLATOR = (event, sequence, args) -> {
        event.setData(args);
        event.setSequence(sequence);
    };

    public void onData(Object ...data) {
        ringBuffer.publishEvent(TRANSLATOR,data);
    }

}