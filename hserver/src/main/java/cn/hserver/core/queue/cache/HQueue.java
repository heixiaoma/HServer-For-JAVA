package cn.hserver.core.queue.cache;

import cn.hserver.core.queue.QueueData;
import cn.hserver.core.server.util.NamedThreadFactory;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HQueue {
    private final ScheduledExecutorService newScheduledThreadPool = Executors.newScheduledThreadPool(1);
    private final CacheMap<QueueData> STORE_MAP;
    private final CacheMap<QueueData> DELAY_MAP;
    private final CacheMap<QueueData> RUN_MAP;

    public HQueue(String path) {
        STORE_MAP = new CacheMap<>(path + File.separator + "sore", QueueData.class);
        RUN_MAP = new CacheMap<>(path + File.separator + "run", QueueData.class);
        DELAY_MAP = new CacheMap<>(path + File.separator + "delay", QueueData.class);
        changeOld();
        checkDelayQueue();
    }

    /**
     * 检查延时队列
     */
    public void checkDelayQueue() {
        Runnable runnable = () -> {

        };
        newScheduledThreadPool.scheduleWithFixedDelay(runnable, 1, 1, TimeUnit.SECONDS);
    }

    /**
     * 上传运行中的数据转换到存储中，重新开始新的计数
     */
    public void changeOld() {
        for (QueueData queueData : RUN_MAP.getAll()) {
            putQueue(queueData);
        }
        RUN_MAP.clear();
    }


    public void clear() {
        STORE_MAP.clear();
        RUN_MAP.clear();
    }

    public void close() {
        STORE_MAP.close();
        RUN_MAP.close();
    }

    /**
     * 添加一个数据到新的存储
     *
     * @param queueData
     */
    public void putQueue(QueueData queueData) {
        STORE_MAP.put(queueData.getQueueId(), queueData);
    }

    /**
     * 添加延时Queue
     *
     * @param queueData
     * @param seconds
     */
    public void putDelayQueue(QueueData queueData, int seconds) {
        DELAY_MAP.put(queueData.getQueueId(), queueData);
    }

    /**
     * 添加一个已经存在的数据到运行中的存储
     *
     * @param queueData
     */
    private void putRun(QueueData queueData) {
        RUN_MAP.put(queueData.getQueueId(), queueData);
    }


    /**
     * 队列总大小
     *
     * @return
     */
    public int size() {
        return STORE_MAP.size() + RUN_MAP.size();
    }

    /**
     * 删除运行中的数据
     *
     * @param uid
     */
    public void removeRun(String uid) {
        RUN_MAP.remove(uid);
    }

    public void removeAllQueueId(String queueId) {
        RUN_MAP.remove(queueId);
        DELAY_MAP.remove(queueId);
        STORE_MAP.remove(queueId);
    }


    /**
     * 获取存储中第一个数据
     *
     * @return
     */
    public QueueData getFirst() {
        QueueData first = STORE_MAP.getFirst();
        if (first == null) {
            return null;
        }
        //添加到运行中
        putRun(first);
        //删除自己
        STORE_MAP.remove(first.getQueueId());
        return first;
    }
}
