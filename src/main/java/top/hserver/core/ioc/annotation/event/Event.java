package top.hserver.core.ioc.annotation.event;

import top.hserver.core.eventx.EventPriority;

import java.lang.annotation.*;

/**
 * 注解：事件。用于事件处理方法<br>
 * 用法： Event(value = "事件名", priority = EventPriority.XXX)
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Event {
	String value() default "";

	int priority() default EventPriority.HIGH;
}
