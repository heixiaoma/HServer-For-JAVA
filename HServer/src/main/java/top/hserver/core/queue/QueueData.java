package top.hserver.core.queue;


import top.hserver.core.queue.fqueue.FQueue;

import java.io.Serializable;

/**
 * @author hxm
 */
public class QueueData implements Serializable {

    private FQueue fQueue;

    private String queueName;

    private Object[] args;

    public QueueData() {
    }

    public QueueData(String queueName, Object[] args,FQueue fQueue) {
        this.queueName = queueName;
        this.args = args;
        this.fQueue=fQueue;
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
