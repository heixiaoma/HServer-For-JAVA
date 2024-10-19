package cn.hserver.core.queue;

public class QueueInfo {
    //剩余数
    private final long size;
    private final int threadSize;
    private final String queueName;

    public QueueInfo(long size,int threadSize,String queueName) {
        this.size = size;
        this.threadSize = threadSize;
        this.queueName = queueName;
    }

    public long getSize() {
        return size;
    }

    public String getQueueName() {
        return queueName;
    }

    public int getThreadSize() {
        return threadSize;
    }
}
