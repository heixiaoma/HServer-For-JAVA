package cn.hserver.plugin.web.annotation;

import java.lang.annotation.*;

/**
 * @author hxm
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebSocket {
    String value();
}
