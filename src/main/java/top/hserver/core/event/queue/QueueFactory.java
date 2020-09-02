package top.hserver.core.event.queue;


import top.hserver.core.event.EventHandleMethod;

import java.util.LinkedList;

public interface QueueFactory {

    void createQueue(String queueName, int bufferSize, LinkedList<EventHandleMethod> eventHandleMethods);

    void producer(EventHandleMethod eventHandleMethod);

}
