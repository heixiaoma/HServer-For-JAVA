package cn.hserver.core.server.limit;


import com.google.common.util.concurrent.RateLimiter;
import cn.hserver.core.interfaces.Limit;
import cn.hserver.core.interfaces.LimitAdapter;
import cn.hserver.core.server.context.Webkit;

import java.util.concurrent.ConcurrentHashMap;

public abstract class UrlLimit extends Limit implements LimitAdapter {

    private static ConcurrentHashMap<String, RateLimiter> rateLimiterPool = new ConcurrentHashMap<>();

    private int qps;

    public UrlLimit(int qps) {
        this.qps = qps;
    }

    @Override
    public void doLimit(Webkit webkit) throws Exception {
        String key = webkit.httpRequest.getUri();
        if (rateLimiterPool.containsKey(key)) {
            RateLimiter rateLimiter = rateLimiterPool.get(key);
            result(webkit, rateLimiter.getRate(), !rateLimiter.tryAcquire());
        } else {
            rateLimiterPool.put(key, RateLimiter.create(qps));
        }
    }
}
