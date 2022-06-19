package net.hserver.core.queue;


import net.hserver.core.queue.fqueue.FQueue;

import java.io.Serializable;

/**
 * @author hxm
 */
public class QueueData implements Serializable {

    private FQueue fQueue;

    private String queueName;

    private Object[] args;

    private int threadSize;

    public QueueData() {
    }

    public QueueData(String queueName, Object[] args,FQueue fQueue) {
        this.queueName = queueName;
        this.args = args;
        this.fQueue=fQueue;
    }

    public int getThreadSize() {
        return threadSize;
    }

    public void setThreadSize(int threadSize) {
        this.threadSize = threadSize;
    }

    public FQueue getfQueue() {
        return fQueue;
    }

    public void setfQueue(FQueue fQueue) {
        this.fQueue = fQueue;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
