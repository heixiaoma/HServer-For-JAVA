package cn.hserver.plugin.beetlsql.annotation;


import java.lang.annotation.*;

/**
 * @author hxm
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BeetlSQL {
    String value() default "";
}
