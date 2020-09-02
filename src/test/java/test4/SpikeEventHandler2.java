package test4;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

public class SpikeEventHandler2 implements EventHandler<Event>, WorkHandler<Event> {


    @Override
    public void onEvent(Event event, long sequence, boolean endOfBatch) throws Exception {
        System.out.println(event.getData());
    }


    @Override
    public void onEvent(Event event) throws Exception {
        System.out.println("2-" + event.getData());
    }
}