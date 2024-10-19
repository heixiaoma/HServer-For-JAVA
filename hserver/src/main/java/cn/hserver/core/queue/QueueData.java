package cn.hserver.core.queue;



import java.io.Serializable;

/**
 * @author hxm
 */
public class QueueData implements Serializable {

    private String queueName;

    private Object[] args;

    public QueueData(String queueName, Object[] args) {
        this.queueName = queueName;
        this.args = args;
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
