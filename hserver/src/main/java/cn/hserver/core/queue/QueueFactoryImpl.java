package cn.hserver.core.queue;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import cn.hserver.core.ioc.annotation.queue.QueueHandlerType;
import cn.hserver.core.server.util.NamedThreadFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hxm
 */
public class QueueFactoryImpl implements QueueFactory {

    private Disruptor<QueueData> disruptor;

    @Override
    public void createQueue(String queueName, int bufferSize, QueueHandlerType queueHandlerType, List<QueueHandleMethod> queueHandleMethods) {
        // 创建disruptor
        disruptor = new Disruptor<>(QueueData::new, bufferSize, new NamedThreadFactory("queue:" + queueName));
        Map<Integer, List<QueueHandleMethod>> collect = queueHandleMethods.stream().sorted(Comparator.comparingInt(QueueHandleMethod::getLevel)).collect(Collectors.groupingBy(QueueHandleMethod::getLevel));

        EventHandlerGroup<QueueData> eventHandlerGroup = null;

        Iterator<Integer> iterator = collect.keySet().iterator();
        int flag = 0;
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            List<QueueHandleMethod> handleMethods = collect.get(next);
            //检查哈是否有那种设置了多个消费者的添加进去，多线程那种意思
            for (int i = 0; i < handleMethods.size(); i++) {
                QueueHandleMethod queueHandleMethod = handleMethods.get(i);
                int size = queueHandleMethod.getSize();
                if (size > 1) {
                    for (int j = 0; j < size - 1; j++) {
                        handleMethods.add(queueHandleMethod);
                    }
                    queueHandleMethod.setSize(1);
                }
            }
            if (flag == 0) {
                QueueEventHandler[] queueEventHandlers = new QueueEventHandler[handleMethods.size()];
                for (int i = 0; i < handleMethods.size(); i++) {
                    QueueHandleMethod queueHandleMethod = handleMethods.get(i);
                    queueEventHandlers[i] = new QueueEventHandler(queueName, queueHandleMethod.getMethod());
                }
                //多消费者重复消费
                if (queueHandlerType == QueueHandlerType.REPEAT) {
                    eventHandlerGroup = disruptor.handleEventsWith(queueEventHandlers);
                } else {
                    //多消费者不重复消费
                    eventHandlerGroup = disruptor.handleEventsWithWorkerPool(queueEventHandlers);
                }
                flag++;
            } else {
                QueueEventHandler[] queueEventHandlers = new QueueEventHandler[handleMethods.size()];
                for (int i = 0; i < handleMethods.size(); i++) {
                    QueueHandleMethod queueHandleMethod = handleMethods.get(i);
                    queueEventHandlers[i] = new QueueEventHandler(queueName, queueHandleMethod.getMethod());
                }
                //多消费者重复消费
                if (queueHandlerType == QueueHandlerType.REPEAT) {
                    eventHandlerGroup.then(queueEventHandlers);
                } else {
                    //多消费者不重复消费
                    eventHandlerGroup.thenHandleEventsWithWorkerPool(queueEventHandlers);
                }
            }
        }
    }


    @Override
    public void start() {
        disruptor.start();
    }

    @Override
    public void stop() {
        disruptor.shutdown();
    }

    @Override
    public void producer(QueueData queueData) {
        RingBuffer<QueueData> ringBuffer = disruptor.getRingBuffer();
        long sequence = ringBuffer.next();
        try {
            QueueData rdata = ringBuffer.get(sequence);
            rdata.setArgs(queueData.getArgs());
            rdata.setfQueue(queueData.getfQueue());
            rdata.setQueueName(queueData.getQueueName());
            rdata.setThreadSize(queueData.getThreadSize());
        } finally {
            ringBuffer.publish(sequence);
        }
    }

    @Override
    public QueueInfo queueInfo() {
        QueueInfo queueInfo = new QueueInfo();
        queueInfo.setBufferSize(disruptor.getBufferSize());
        queueInfo.setCursor(disruptor.getCursor());
        queueInfo.setRemainQueueSize(disruptor.getRingBuffer().remainingCapacity());
        return queueInfo;
    }
}