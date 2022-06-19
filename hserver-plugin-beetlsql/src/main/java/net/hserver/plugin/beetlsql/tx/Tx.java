package net.hserver.plugin.beetlsql.tx;

import java.lang.annotation.*;

/**
 * @author hxm
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Tx {

    int timeoutMillisecond() default -1;

    Class<? extends Throwable>[] rollbackFor() default {};
}
