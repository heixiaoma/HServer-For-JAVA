package top.hserver.core.queue;


/**
 * 队列调用
 *
 * @author hxm
 */
public class HServerQueue {

    /**
     * 发送队列
     *
     * @param queueName 队列名
     * @param args      参数
     */
    public static void sendQueue(String queueName, Object... args) {
        QueueDispatcher.dispatcherQueue(queueName, args);
    }


    /**
     * 发送队列进行持久化
     *
     * @param queueName
     * @param args
     */
    public static void sendPersistQueue(String queueName, Object... args) {
        QueueDispatcher.dispatcherSerializationQueue(queueName, args);
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
