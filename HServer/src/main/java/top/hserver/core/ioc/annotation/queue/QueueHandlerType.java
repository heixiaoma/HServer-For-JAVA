package top.hserver.core.ioc.annotation.queue;

/**
 * 消费者事件的类型
 */
public enum QueueHandlerType {
    /**
     * 不重复消费
     */
    NO_REPEAT,
    /**
     * 重复消费
     */
    REPEAT,
}
