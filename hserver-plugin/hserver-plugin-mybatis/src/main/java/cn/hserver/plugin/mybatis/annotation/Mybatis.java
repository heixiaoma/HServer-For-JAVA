package cn.hserver.plugin.mybatis.annotation;


import java.lang.annotation.*;

/**
 * @author hxm
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mybatis {
    String value() default "";
}
