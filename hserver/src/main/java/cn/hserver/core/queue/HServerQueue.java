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
     * 删除Queue
     *
     * @param queueName
     */
    public static void removeQueue(String queueName) {
        QueueDispatcher.removeQueue(queueName,true);
    }


    public static void removeQueue(String queueName,boolean trueDeleteData) {
        QueueDispatcher.removeQueue(queueName,trueDeleteData);
    }

    /**
     * 停止队列得数据处理
     * @param queueName
     */
    public static void stopHandler(String queueName) {
        QueueDispatcher.stopHandler(queueName);
    }

    /**
     * 启用队列得数据处理
     * @param queueName
     */
    public static void restartHandler(String queueName) {
        QueueDispatcher.restartHandler(queueName);
    }

    /**
     * 发送队列
     *
     * @param queueName 队列名
     * @param args      参数
     */
    public static boolean sendQueue(String queueName, Object... args) {
        return QueueDispatcher.dispatcherSerializationQueue(queueName, args);
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
