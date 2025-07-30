package cn.hserver.core.queue;

import cn.hserver.core.queue.bean.QueueData;
import cn.hserver.core.queue.bean.QueueHandleInfo;
import cn.hserver.core.queue.bean.QueueInfo;
import cn.hserver.core.queue.fqueue.FQueue;
import cn.hserver.core.queue.fqueue.exception.FileFormatException;
import cn.hserver.core.util.SerializationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cn.hserver.core.config.ConstConfig.PERSIST_PATH;


/**
 * @author hxm
 */
public class QueueDispatcher {
    private static final Logger log = LoggerFactory.getLogger(QueueDispatcher.class);
    private static final Map<String, QueueHandleInfo> handleMethodMap = new ConcurrentHashMap<>();
    private static final Map<String, FQueue> FQ = new ConcurrentHashMap<>();

    private QueueDispatcher() {
    }

    public static QueueHandleInfo getQueueHandleInfo(String queueName) {
        return handleMethodMap.get(queueName);
    }

    public static void removeQueue(String queueName, boolean trueDelete) {
        handleMethodMap.remove(queueName);
        FQueue fQueue = FQ.get(queueName);
        if (fQueue != null) {
            if (trueDelete) {
                fQueue.clear();
            }
            try {
                fQueue.close();
            } catch (IOException | FileFormatException e) {
                log.error(e.getMessage(), e);
            }
        }
        FQ.remove(queueName);
    }

    public static void stopHandler(String queueName) {
        FQueue fQueue = FQ.get(queueName);
        if (fQueue != null) {
            fQueue.stopHandler();
        }
    }

    public static void restartHandler(String queueName) {
        FQueue fQueue = FQ.get(queueName);
        if (fQueue != null) {
            fQueue.restartHandler();
        }
    }



    public static List<String> getAllQueueName() {
        return new ArrayList<>(FQ.keySet());
    }


    public static void addQueueListener(QueueHandleInfo eventHandleInfo) {
        handleMethodMap.put(eventHandleInfo.getQueueName(), eventHandleInfo);
    }



    /**
     * 持久化存储
     *
     * @param queueName
     * @param args
     */
    public static boolean dispatcherSerializationQueue(String queueName, Object... args) {
        FQueue fQueue = FQ.get(queueName);
        if (fQueue == null) {
            log.error("不存在:{} 队列", queueName);
            return false;
        }
        fQueue.offer(SerializationUtil.serialize(new QueueData(queueName, args)));
        return true;
    }


    public static QueueInfo queueInfo(String queueName) {
        QueueHandleInfo queueHandleInfo = handleMethodMap.get(queueName);
        FQueue fQueue = FQ.get(queueName);
        return new QueueInfo(fQueue.size(), queueHandleInfo.getThreadSize(), queueName);
    }

    public static void startQueueServer(){
        handleMethodMap.forEach((queueName, v) -> {
            FQueue fQueue = null;
            try {
                fQueue = new FQueue(PERSIST_PATH + File.separator + v.getQueueName(), v.getQueueName());
                FQ.put(queueName, fQueue);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return;
            }
            fQueue.start();
        });
    }

}
