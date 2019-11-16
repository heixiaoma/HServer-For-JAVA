package com.hserver.core.ioc.interfaces;

import com.hserver.core.server.context.Request;
import com.hserver.core.server.context.WebContext;
import com.hserver.core.server.filter.FilterChain;

/**
 * 拦截器
 */
public interface FilterAdapter {

    /**
     * 所有拦截先走这里过滤一次
     *
     * @param chain
     * @param webContext
     */
    void doFilter(FilterChain chain , WebContext webContext);
}
