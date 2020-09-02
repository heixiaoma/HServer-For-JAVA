package top.hserver.core.ioc.annotation.event;

/**
 * 消费者事件的类型
 */
public enum EventHandlerType {
    /**
     * 不重复消费
     */
    NO_REPEAT_CONSUMPTION,
    /**
     * 重复消费
     */
    REPEAT_CONSUMPTION,
}
