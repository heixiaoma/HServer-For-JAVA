package test1.filter;

import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.Filter;
import top.hserver.core.interfaces.FilterAdapter;
import top.hserver.core.server.context.Webkit;
import top.hserver.core.server.filter.FilterChain;
import lombok.extern.slf4j.Slf4j;
import test1.service.HelloService;

/**
 * 优先级顺序
 */
@Filter(1)
@Slf4j
public class MyFilter1 implements FilterAdapter {

    @Autowired
    private HelloService helloService;

    @Override
    public void doFilter(FilterChain chain, Webkit webkit) throws Exception{
//        log.info(webkit.httpRequest.getUri());
        if (webkit.httpRequest.getUri().equals("/filter")) {
            webkit.httpResponse.sendJson("我是拦截器拦截"+helloService.sayHello());
        } else {
            chain.doFilter(webkit);
        }
    }
}
