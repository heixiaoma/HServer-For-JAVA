package test1.filter;

import test1.log.Log;
import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.interfaces.FilterAdapter;
import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.server.context.Webkit;
import lombok.extern.slf4j.Slf4j;
import test1.service.HelloService;

/**
 * 优先级顺序
 */
@Slf4j
@Bean
public class MyFilter1 implements FilterAdapter {

    @Autowired
    private HelloService helloService;

    @Override
    public void doFilter(Webkit webkit) throws Exception{
        if (webkit.httpRequest.getUri().equals("/filter")) {
            webkit.httpResponse.sendJson("我是拦截器拦截"+helloService.sayHello());
        }
    }
}
