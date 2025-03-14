package cn.hserver.plugin.web.interfaces;


import cn.hserver.plugin.web.context.Webkit;

/**
 * 拦截器
 * @author hxm
 */
public interface StaticFilterAdapter {

    /**
     * 所有拦截先走这里过滤一次
     * @param webkit
     * @throws Exception
     */
    void doFilter(Webkit webkit) throws Exception;
}
