package cn.hserver.mvc.filter;


import cn.hserver.mvc.context.WebContext;

/**
 * 拦截器
 * @author hxm
 */
public interface FilterAdapter {

    /**
     * 所有拦截先走这里过滤一次
     * @param webContext
     * @throws Exception
     */
    void doFilter(WebContext webContext) throws Exception;
}
