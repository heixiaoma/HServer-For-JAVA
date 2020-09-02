package top.hserver.core.event.queue;


import top.hserver.core.event.EventHandleMethod;
import top.hserver.core.ioc.annotation.event.EventHandlerType;

import java.util.LinkedList;
import java.util.List;

/**
 * @author hxm
 */
public interface QueueFactory {

    void createQueue(String queueName, int bufferSize,EventHandlerType eventHandlerType, List<EventHandleMethod> eventHandleMethods);

    void producer(Object[] args);

}
