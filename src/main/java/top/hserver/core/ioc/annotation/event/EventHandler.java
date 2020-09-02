package top.hserver.core.ioc.annotation.event;

import java.lang.annotation.*;

/**
 * 注解：事件处理器。用于事件处理器类<br>
 * 用法： EventHandler("/模块名")
 *
 * @author hxm
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EventHandler {
    /**
     * 队列名
     *
     * @return
     */
    String queueName();

    /**
     * 消费者类型
     *
     * @return
     */
    EventHandlerType type() default EventHandlerType.NO_REPEAT_CONSUMPTION;

    /**
     * 大小
     * @return
     */
    int bufferSize() default 1024;
}
