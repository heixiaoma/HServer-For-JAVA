package net.hserver.core.queue;

public class QueueInfo {

    //内存剩余队列数
    private long remainQueueSize;

    //最大数量
    private long bufferSize;

    //游标，执行次数
    private long cursor;

    // 持久化的数量
    private long fqueueSize;

    public long getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(long bufferSize) {
        this.bufferSize = bufferSize;
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

    public long getFqueueSize() {
        return fqueueSize;
    }

    public void setFqueueSize(long fqueueSize) {
        this.fqueueSize = fqueueSize;
    }

    @Override
    public String toString() {
        return "QueueInfo{" +
                "remainQueueSize=" + remainQueueSize +
                ", bufferSize=" + bufferSize +
                ", cursor=" + cursor +
                ", fqueueSize=" + fqueueSize +
                '}';
    }
}
