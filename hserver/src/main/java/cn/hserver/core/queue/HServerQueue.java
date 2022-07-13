package cn.hserver.core.queue;


import java.util.List;

/**
 * 队列调用
 *
 * @author hxm
 */
public class HServerQueue {


    /**
     * 动态添加Queue
     *
     * @param queueName
     * @param classz
     */
    public static void addQueueListener(String queueName, Class classz) {
        QueueDispatcher.addQueueListener(queueName, classz);
    }


    /**
     * 获取所有队列名
     */
    public static List<String> getAllQueueName() {
        return QueueDispatcher.getAllQueueName();
    }

    /**
     * 删除队列中得数据
     *
     * @param queueName
     * @param queueId
     */
    public static void removeQueueData(String queueName, String queueId) {
        QueueDispatcher.removeQueueData(queueName, queueId);
    }


    /**
     * 删除Queue
     *
     * @param queueName
     */
    public static void removeQueue(String queueName) {
        QueueDispatcher.removeQueue(queueName, true);
    }

    public static void removeQueue(String queueName, boolean trueDeleteData) {
        QueueDispatcher.removeQueue(queueName, trueDeleteData);
    }

    /**
     * 发送队列
     *
     * @param queueName 队列名
     * @param args      参数
     */
    public static boolean sendQueue(String queueName, Object... args) {
       return sendQueue(queueName, null, args);
    }

    /**
     * 发生队列定义队列ID ，方便后期可以删除
     * @param queueName 队列名字
     * @param queueId 队列ID
     * @param args 参数
     * @return
     */
    public static boolean sendQueue(String queueName, String queueId, Object... args) {
        return QueueDispatcher.dispatcherSerializationQueue(queueName, queueId, args);
    }


    /**
     * 队列信息
     *
     * @param queueName
     * @return
     */
    public static QueueInfo queueInfo(String queueName) {
        return QueueDispatcher.queueInfo(queueName);
    }

}
