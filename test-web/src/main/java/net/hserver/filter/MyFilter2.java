package net.hserver.filter;

import top.hserver.core.interfaces.FilterAdapter;
import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.ioc.annotation.Order;
import top.hserver.core.server.context.Webkit;

/**
 * 优先级顺序
 */
@Order(0)
@Bean
public class MyFilter2 implements FilterAdapter  {

    @Override
    public void doFilter(Webkit webkit) throws Exception {
        System.out.println("MyFilter->2");
    }
}
