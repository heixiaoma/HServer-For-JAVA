package cn.hserver.plugin.web.limit;

import cn.hserver.plugin.web.context.Webkit;
import cn.hserver.plugin.web.interfaces.Limit;
import cn.hserver.plugin.web.interfaces.LimitAdapter;
import com.google.common.util.concurrent.RateLimiter;

public abstract class GlobalLimit extends Limit implements LimitAdapter {

    /**
     * 单机全局限流器(限制QPS为1)
     */
    private static RateLimiter rateLimiter;

    public GlobalLimit(int qps) {
        if (rateLimiter == null) {
            rateLimiter = RateLimiter.create(qps);
        }
    }

    @Override
    public void doLimit(Webkit webkit) throws Exception {
        result(webkit, rateLimiter.getRate(),!rateLimiter.tryAcquire());
    }

}
