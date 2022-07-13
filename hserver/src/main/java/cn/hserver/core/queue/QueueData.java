package cn.hserver.core.queue;



import cn.hserver.core.queue.cache.CacheMap;
import cn.hserver.core.queue.cache.HQueue;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author hxm
 */
public class QueueData implements Serializable {

    private HQueue hQueue;

    private String uid;

    private String queueName;

    private Object[] args;


    public QueueData() {
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public QueueData(String queueName, Object[] args) {
        this.queueName = queueName;
        this.args = args;
        uid= UUID.randomUUID().toString();
    }

    public String getUid() {
        return uid;
    }

    public HQueue gethQueue() {
        return hQueue;
    }

    public void sethQueue(HQueue hQueue) {
        this.hQueue = hQueue;
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
