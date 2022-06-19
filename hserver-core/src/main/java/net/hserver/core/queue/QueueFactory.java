package net.hserver.core.queue;


import net.hserver.core.ioc.annotation.queue.QueueHandlerType;

import java.util.List;

/**
 * @author hxm
 */
public interface QueueFactory {

    void createQueue(String queueName, int bufferSize, QueueHandlerType queueHandlerType, List<QueueHandleMethod> queueHandleMethods);

    void producer(QueueData queueData);

    QueueInfo queueInfo();

    void start();

    void stop();

}
