package test4;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

import javax.annotation.Resource;

public class SpikeEventHandler1 implements EventHandler<Event>,WorkHandler<Event> {


    @Override
    public void onEvent(Event event, long sequence, boolean endOfBatch) throws Exception {
        System.out.println(event.getData());
    }


    @Override
    public void onEvent(Event event) throws Exception {
        Thread.sleep(4000);
        System.out.println("1-"+event.getData());
    }
}