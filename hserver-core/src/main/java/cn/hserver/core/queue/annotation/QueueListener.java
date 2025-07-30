package cn.hserver.core.queue.annotation;

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
    String queueName();

    /**
     * 线程数
     *
     * @return
     */
    int threadSize() default 1;
}
