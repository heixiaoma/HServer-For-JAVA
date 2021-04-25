package top.hserver.core.ioc.annotation;

import io.netty.handler.codec.http.HttpRequest;

import java.lang.annotation.*;

/**
 * @author hxm
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebSocketClient {
    String url();
}
