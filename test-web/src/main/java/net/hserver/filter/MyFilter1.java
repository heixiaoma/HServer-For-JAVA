package net.hserver.filter;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import net.hserver.service.HelloService;
import top.hserver.core.interfaces.FilterAdapter;
import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.ioc.annotation.Order;
import top.hserver.core.server.context.Webkit;

/**
 * 优先级顺序
 */
@Order(1)
@Bean
public class MyFilter1 implements FilterAdapter {

    @Autowired
    private HelloService helloService;

    @Override
    public void doFilter(Webkit webkit) throws Exception{

        if(webkit.httpRequest.getRequestType().equals(HttpMethod.OPTIONS)){
            //webkit.httpResponse.sendStatusCode(HttpResponseStatus.NO_CONTENT);
            webkit.httpResponse.sendText("");
        }

        if (webkit.httpRequest.getUri().equals("/filter")) {
            webkit.httpResponse.sendJson("我是拦截器拦截"+helloService.sayHello());
        }
    }
}
