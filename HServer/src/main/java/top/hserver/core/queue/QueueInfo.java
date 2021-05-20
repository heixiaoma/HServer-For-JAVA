package top.hserver.core.queue;

public class QueueInfo {

    private long queueSize;

    private long remainQueueSize;

    private long cursor;

    private long fqueue;

    public long getFqueue() {
        return fqueue;
    }

    public void setFqueue(long fqueue) {
        this.fqueue = fqueue;
    }

    public long getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(long queueSize) {
        this.queueSize = queueSize;
    }

    public long getRemainQueueSize() {
        return remainQueueSize;
    }

    public void setRemainQueueSize(long remainQueueSize) {
        this.remainQueueSize = remainQueueSize;
    }

    public long getCursor() {
        return cursor;
    }

    public void setCursor(long cursor) {
        this.cursor = cursor;
    }
}
