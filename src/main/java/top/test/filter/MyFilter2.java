package top.test.filter;

import top.hserver.core.ioc.annotation.Filter;
import top.hserver.core.interfaces.FilterAdapter;
import top.hserver.core.server.context.Webkit;
import top.hserver.core.server.filter.FilterChain;
import lombok.extern.slf4j.Slf4j;

/**
 * 优先级顺序
 */
@Filter(2)
@Slf4j
public class MyFilter2 implements FilterAdapter  {

    @Override
    public void doFilter(FilterChain chain, Webkit webkit) throws Exception {
//        log.info("MyFilter->2");
        chain.doFilter(webkit);
    }
}
