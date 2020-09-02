package top.hserver.core.event.queue;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import top.hserver.core.event.EventHandleMethod;

public class QueueEventHandler implements EventHandler<EventHandleMethod>, WorkHandler<EventHandleMethod> {

    private String name;

    public QueueEventHandler(String name) {
        this.name = name;
    }

    @Override
    public void onEvent(EventHandleMethod event, long sequence, boolean endOfBatch) throws Exception {
    }


    @Override
    public void onEvent(EventHandleMethod event) throws Exception {
    }
}