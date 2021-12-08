package top.hserver.core.queue;

import top.hserver.core.ioc.annotation.queue.QueueHandlerType;

import java.util.ArrayList;
import java.util.List;

/**
 * 队列信息
 *
 * @author hxm
 */
public class QueueHandleInfo {

    private QueueFactory queueFactory;

    private String queueName;

    private int bufferSize;

    private QueueHandlerType queueHandlerType;

    private int threadSize;

    private List<QueueHandleMethod> queueHandleMethods = new ArrayList<>();

    public void add(QueueHandleMethod eventHandleMethod) {
        this.queueHandleMethods.add(eventHandleMethod);
    }

    public QueueFactory getQueueFactory() {
        return queueFactory;
    }

    public void setQueueFactory(QueueFactory queueFactory) {
        this.queueFactory = queueFactory;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public int getThreadSize() {
        return threadSize;
    }

    public void setThreadSize(int threadSize) {
        this.threadSize = threadSize;
    }

    public QueueHandlerType getQueueHandlerType() {
        return queueHandlerType;
    }

    public void setQueueHandlerType(QueueHandlerType queueHandlerType) {
        this.queueHandlerType = queueHandlerType;
    }

    public List<QueueHandleMethod> getQueueHandleMethods() {
        return queueHandleMethods;
    }

}
