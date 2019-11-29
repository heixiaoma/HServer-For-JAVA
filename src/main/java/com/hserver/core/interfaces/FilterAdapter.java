package com.hserver.core.interfaces;

import com.hserver.core.server.context.Webkit;
import com.hserver.core.server.filter.FilterChain;

/**
 * 拦截器
 */
public interface FilterAdapter {

    /**
     * 所有拦截先走这里过滤一次
     *
     * @param chain
     * @param webkit
     */
    void doFilter(FilterChain chain , Webkit webkit);
}
