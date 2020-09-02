package top.hserver.core.event;

import top.hserver.core.event.queue.QueueFactory;
import top.hserver.core.ioc.annotation.event.EventHandlerType;

import java.util.LinkedList;

/**
 * 队列信息
 *
 * @author hxm
 */
public class EventHandleInfo {

    private QueueFactory queueFactory;

    private String queueName;

    private int bufferSize;

    private EventHandlerType eventHandlerType;

    private LinkedList<EventHandleMethod> eventHandleMethods = new LinkedList<>();

    public void add(EventHandleMethod eventHandleMethod) {
        this.eventHandleMethods.add(eventHandleMethod);
    }

    public EventHandlerType getEventHandlerType() {
        return eventHandlerType;
    }

    public void setEventHandlerType(EventHandlerType eventHandlerType) {
        this.eventHandlerType = eventHandlerType;
    }

    public LinkedList<EventHandleMethod> getEventHandleMethods() {
        return eventHandleMethods;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public QueueFactory getQueueFactory() {
        return queueFactory;
    }

    public void setQueueFactory(QueueFactory queueFactory) {
        this.queueFactory = queueFactory;
    }
}
