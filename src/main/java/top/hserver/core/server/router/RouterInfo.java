package top.hserver.core.server.router;


import io.netty.handler.codec.http.HttpMethod;
import lombok.Data;

import java.lang.reflect.Method;
/**
 * @author hxm
 */
@Data
public class RouterInfo {

    String url;
    Method method;
    HttpMethod reqMethodName;
    Class<?> aClass;

}
