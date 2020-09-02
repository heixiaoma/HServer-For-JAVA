package top.hserver.core.event;

/**
 * 队列调用
 *
 * @author hxm
 */
public class HServerEvent {

    /**
     * 发送事件
     *
     * @param queueName 队列名
     * @param args      参数
     */
    public static void sendEvent(String queueName, Object... args) {
        EventDispatcher.dispatcherEvent(queueName, args);
    }

}
