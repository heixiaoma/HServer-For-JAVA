package top.hserver.core.ioc.annotation.queue;

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
public @interface QueueListener {
    /**
     * 队列名
     *
     * @return
     */
    String queueName() default "";

    /**
     * 消费者类型
     *
     * @return
     */
    QueueHandlerType type() default QueueHandlerType.NO_REPEAT;

    /**
     * 队列默认长度
     *
     * @return
     */
    int bufferSize() default 1024;
}
