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
	String value() default "";
}
