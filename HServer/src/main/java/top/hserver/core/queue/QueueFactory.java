package top.hserver.core.queue;


import top.hserver.core.ioc.annotation.queue.QueueHandlerType;
import top.hserver.core.queue.fqueue.FQueue;

import java.util.List;

/**
 * @author hxm
 */
public interface QueueFactory {

    void createQueue(String queueName, int bufferSize, QueueHandlerType queueHandlerType, List<QueueHandleMethod> queueHandleMethods);

    void producer(QueueData queueData);

    QueueInfo queueInfo();

}
