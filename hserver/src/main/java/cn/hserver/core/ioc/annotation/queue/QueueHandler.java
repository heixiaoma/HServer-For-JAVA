package cn.hserver.core.ioc.annotation.queue;

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
    int size() default 1;
    //可以配置
    String sizePropValue() default "";

}
