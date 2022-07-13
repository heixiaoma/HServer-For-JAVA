package cn.hserver.core.queue.cache;

import cn.hserver.HServerApplication;
import cn.hserver.core.queue.ListQueueData;
import cn.hserver.core.queue.QueueData;
import cn.hserver.core.server.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HQueue {
    private static final Logger log = LoggerFactory.getLogger(HQueue.class);

    private final ScheduledExecutorService newScheduledThreadPool = Executors.newScheduledThreadPool(1);
    private final CacheMap<QueueData> STORE_MAP;
    private final CacheMap<ListQueueData> DELAY_MAP;
    //一天这么多分秒
    private final CacheMap<QueueData> RUN_MAP;

    public HQueue(String path) {
        STORE_MAP = new CacheMap<>(path + File.separator + "sore", QueueData.class);
        RUN_MAP = new CacheMap<>(path + File.separator + "run", QueueData.class);
        DELAY_MAP = new CacheMap<>(path + File.separator + "delay", ListQueueData.class);
        changeOld();
        checkDelayQueue();
    }

    /**
     * 检查延时队列
     */
    public void checkDelayQueue() {
        Runnable runnable = () -> {
            //获取当前时间是一天得地几秒
            Calendar calendar = Calendar.getInstance();
            int currentSecond = calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND);
            String id = String.valueOf(currentSecond);
            try {
                ListQueueData listQueueData = DELAY_MAP.get(id);
                if (listQueueData != null && listQueueData.getQueueDataList() != null && !listQueueData.getQueueDataList().isEmpty()) {
                    List<QueueData> temp = new ArrayList<>();
                    for (QueueData queueData : listQueueData.getQueueDataList()) {
                        if (queueData.getCycleNum() <= 0) {
                            putQueue(queueData);
                        } else {
                            queueData.countDown();
                            temp.add(queueData);
                        }
                    }
                    listQueueData.getQueueDataList().clear();
                    DELAY_MAP.remove(id);
                    DELAY_MAP.put(id, new ListQueueData(id, temp));
                }
            } catch (Exception e) {
                log.error(ExceptionUtil.getMessage(e));
            }

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
        Calendar calendar = Calendar.getInstance();
        int currentSecond = calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND);
        //下轮时间
        int soltIndex = (currentSecond + seconds) % 3600;
        queueData.setCycleNum(seconds / 3600);
        String delayId = String.valueOf(soltIndex);
        ListQueueData listQueueData = DELAY_MAP.get(delayId);
        if (listQueueData == null) {
            listQueueData = new ListQueueData(delayId, queueData);
        } else {
            listQueueData.addData(queueData);
        }
        DELAY_MAP.put(delayId, listQueueData);
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
        STORE_MAP.remove(queueId);
        List<ListQueueData> all = DELAY_MAP.getAll();
        for (ListQueueData listQueueData : all) {
            List<QueueData> queueDataList = listQueueData.getQueueDataList();
            if (queueDataList != null) {
                QueueData temp = null;
                for (QueueData queueData : queueDataList) {
                    if (queueData.getQueueId().equals(queueId)) {
                        temp = queueData;
                        break;
                    }
                }
                if (temp != null) {
                    queueDataList.remove(temp);
                    DELAY_MAP.put(listQueueData.getId(), listQueueData);
                    break;
                }
            }
        }
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
