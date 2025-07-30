package cn.hserver.core.queue.bean;

import cn.hserver.core.queue.QueueEventHandler;

/**
 * 处理器信息
 *
 * @author hxm
 */
public class QueueHandleInfo {
    private final String queueName;
    private int threadSize;
    private QueueEventHandler queueEventHandler;

    public QueueHandleInfo(String queueName) {
        this.queueName = queueName;
    }

    public String getQueueName() {
        return queueName;
    }

    public int getThreadSize() {
        return threadSize;
    }

    public QueueEventHandler getQueueEventHandler() {
        return queueEventHandler;
    }

    public void setThreadSize(int threadSize) {
        this.threadSize = threadSize;
    }

    public void setQueueEventHandler(QueueEventHandler queueEventHandler) {
        this.queueEventHandler = queueEventHandler;
    }


}
