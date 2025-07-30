package cn.hserver.core.queue.annotation;

import java.lang.annotation.*;

/**
 * 标记消费者的方法
 *
 * @author hxm
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface QueueHandler {
}
