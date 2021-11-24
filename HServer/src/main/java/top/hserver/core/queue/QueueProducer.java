package top.hserver.core.queue;

import com.lmax.disruptor.RingBuffer;


/**
 * @author hxm
 */
public class QueueProducer {

    private final RingBuffer<QueueData> ringBuffer;

    public QueueProducer(RingBuffer<QueueData> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(QueueData qd) {
        long sequence = ringBuffer.next();
        try {
            QueueData queueData = ringBuffer.get(sequence);
            queueData.setArgs(qd.getArgs());
            queueData.setfQueue(qd.getfQueue());
            queueData.setQueueName(qd.getQueueName());
        } finally {
            ringBuffer.publish(sequence);
        }
    }

}