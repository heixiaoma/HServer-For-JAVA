package top.hserver.core.queue;

import lombok.Data;

@Data
public class QueueInfo {

    private long queueSize;

    private long remainQueueSize;

    private long cursor;

}
