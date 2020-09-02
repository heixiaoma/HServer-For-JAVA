package test4;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

import javax.annotation.Resource;

public class SpikeEventHandler implements EventHandler<Event>, WorkHandler<Event> {

    private String name;

    public SpikeEventHandler(String name) {
        this.name = name;
    }

    @Override
    public void onEvent(Event event, long sequence, boolean endOfBatch) throws Exception {
        System.out.println(name + event.getData());
    }


    @Override
    public void onEvent(Event event) throws Exception {
        System.out.println(name + event.getData());
    }
}