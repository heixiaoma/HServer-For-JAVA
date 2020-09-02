package top.hserver.core.ioc.annotation.event;


import java.lang.annotation.*;

/**
 * 标记消费者的方法
 *
 * @author hxm
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Event {
    //消费者优先级 级别重小到大排序，小的有限，同一样的就并行操作
    int level() default 1;

    //消费者数量
    int size() default 1;
}
