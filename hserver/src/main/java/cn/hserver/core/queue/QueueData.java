package cn.hserver.core.queue;



import cn.hserver.core.queue.cache.CacheMap;
import cn.hserver.core.queue.cache.HQueue;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author hxm
 */
public class QueueData implements Serializable {

    private HQueue hQueue;

    private String queueId;

    private String queueName;

    private Object[] args;
    //用于延时队列处理数据
    private Integer cycleNum;


    public QueueData() {

    }

    public QueueData(String queueName, String queueId,Object[] args) {
        this.queueName = queueName;
        this.args = args;
        if (queueId==null) {
            this.queueId = UUID.randomUUID().toString();
        }else {
            this.queueId=queueId;
        }
    }

    public void countDown() {
        this.cycleNum--;
    }

    public Integer getCycleNum() {
        return cycleNum;
    }


    public void setCycleNum(Integer cycleNum) {
        this.cycleNum = cycleNum;
    }
    public String getQueueId() {
        return queueId;
    }

    public void setQueueId(String queueId) {
        this.queueId = queueId;
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

    @Override
    public String toString() {
        return "QueueData{" +
                "hQueue=" + hQueue +
                ", queueId='" + queueId + '\'' +
                ", queueName='" + queueName + '\'' +
                ", args=" + Arrays.toString(args) +
                ", cycleNum=" + cycleNum +
                '}';
    }
}
