package cn.hserver.core.queue;


import cn.hserver.core.queue.bean.QueueInfo;

import java.util.List;

/**
 * 队列调用
 *
 * @author hxm
 */
public class HServerQueue {

    /**
     * 获取所有队列名
     */
    public static List<String> getAllQueueName() {
       return QueueManager.getAllQueueName();
    }
    /**
     * 删除Queue
     *
     * @param queueName
     */
    public static void removeQueue(String queueName) {
        QueueManager.removeQueue(queueName,true);
    }


    public static void removeQueue(String queueName,boolean trueDeleteData) {
        QueueManager.removeQueue(queueName,trueDeleteData);
    }

    /**
     * 停止队列得数据处理
     * @param queueName
     */
    public static void stopHandler(String queueName) {
        QueueManager.stopHandler(queueName);
    }

    /**
     * 启用队列得数据处理
     * @param queueName
     */
    public static void restartHandler(String queueName) {
        QueueManager.restartHandler(queueName);
    }

    /**
     * 发送队列
     *
     * @param queueName 队列名
     * @param args      参数
     */
    public static boolean sendQueue(String queueName, Object... args) {
        return QueueManager.dispatcherSerializationQueue(queueName, args);
    }

    /**
     * 队列信息
     *
     * @param queueName
     * @return
     */
    public static QueueInfo queueInfo(String queueName) {
        return QueueManager.queueInfo(queueName);
    }

}
