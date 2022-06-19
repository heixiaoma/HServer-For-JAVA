package cn.hserver.core.server.limit;

import com.google.common.util.concurrent.RateLimiter;
import cn.hserver.core.interfaces.Limit;
import cn.hserver.core.interfaces.LimitAdapter;
import cn.hserver.core.server.context.Webkit;

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
