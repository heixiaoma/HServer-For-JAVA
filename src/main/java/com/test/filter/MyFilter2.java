package com.test.filter;

import com.hserver.core.ioc.annotation.Filter;
import com.hserver.core.ioc.interfaces.FilterAdapter;
import com.hserver.core.server.context.WebContext;
import com.hserver.core.server.filter.FilterChain;
import lombok.extern.slf4j.Slf4j;

/**
 * 优先级顺序
 */
@Filter(2)
@Slf4j
public class MyFilter2 implements FilterAdapter {

    @Override
    public void doFilter(FilterChain chain, WebContext webContext) {
//        log.info("MyFilter->2");
        chain.doFilter(webContext);
    }
}
