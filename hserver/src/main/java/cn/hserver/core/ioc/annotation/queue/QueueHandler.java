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
    //消费者优先级 级别重小到大排序，小的优先，同一样的就并行操作
    int level() default 1;

    int size() default 1;

    //可以配置
    String sizePropValue() default "";

}
