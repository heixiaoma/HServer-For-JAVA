package top.hserver.core.queue;



import top.hserver.core.server.util.SnowflakeIdWorker;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author hxm
 */
public class QueueData implements Serializable {
    private final  static SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);

    private long id;

    private String queueName;

    private Object[] args;


    public QueueData() {
    }

    public QueueData(String queueName, Object[] args) {
        this.queueName = queueName;
        this.args = args;
        this.id= idWorker.nextId();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
