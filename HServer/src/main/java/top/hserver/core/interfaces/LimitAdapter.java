package top.hserver.core.interfaces;

import top.hserver.core.server.context.Webkit;

/**
 * 拦截器
 * @author hxm
 */
public interface LimitAdapter {

    /**
     * 所有拦截先走这里过滤一次
     * @param webkit
     * @throws Exception
     */
    void doLimit(Webkit webkit) throws Exception;
}
