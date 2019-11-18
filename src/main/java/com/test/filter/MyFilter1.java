package com.test.filter;

import com.hserver.core.ioc.annotation.Filter;
import com.hserver.core.interfaces.FilterAdapter;
import com.hserver.core.server.context.WebContext;
import com.hserver.core.server.context.Webkit;
import com.hserver.core.server.filter.FilterChain;
import lombok.extern.slf4j.Slf4j;

/**
 * 优先级顺序
 */
@Filter(1)
@Slf4j
public class MyFilter1 implements FilterAdapter {

    @Override
    public void doFilter(FilterChain chain, Webkit webkit) {
        log.info(webkit.httpRequest.getUri());
        chain.doFilter(webkit);
    }
}
