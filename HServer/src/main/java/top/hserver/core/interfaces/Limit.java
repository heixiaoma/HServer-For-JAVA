package top.hserver.core.interfaces;

import top.hserver.core.server.context.Webkit;

public abstract class Limit {

    /**
     *
     * @param webkit
     * @param rate 当前速率
     * @param status true 被限制
     */
    public abstract void result(Webkit webkit,Double rate, boolean status);

}
