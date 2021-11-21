package top.hserver.core.queue;


import java.io.Serializable;

/**
 * @author hxm
 */
public class QueueData implements Serializable {

    private String id;

    private String queueName;

    private Object[] args;

    public QueueData() {
    }

    public QueueData(String queueName, Object[] args,String id) {
        this.queueName = queueName;
        this.args = args;
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
