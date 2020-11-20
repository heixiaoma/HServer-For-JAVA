package top.hserver.core.queue;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hxm
 */
@Data
public class QueueData implements Serializable {

    private String queueName;

    private Object[] args;

    public QueueData() {
    }

    public QueueData(String queueName, Object[] args) {
        this.queueName = queueName;
        this.args = args;
    }
}
