package top.hserver.core.hook;

import com.google.common.util.concurrent.RateLimiter;
import top.hserver.core.interfaces.HookAdapter;
import top.hserver.core.ioc.annotation.Hook;
import top.hserver.core.ioc.annotation.QpsLimit;
import top.hserver.core.server.exception.QpsException;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

@Hook(QpsLimit.class)
public class QpsHook implements HookAdapter {

    private static final ConcurrentHashMap<Integer, RateLimiter> rateLimiterPool = new ConcurrentHashMap<>();

    @Override
    public void before(Class clazz, Method method, Object[] args) throws Exception{
        QpsLimit qpsLimit = method.getAnnotation(QpsLimit.class);
        if (qpsLimit!=null){
            int hashCode = method.hashCode();
            if (rateLimiterPool.containsKey(hashCode)) {
                RateLimiter rateLimiter = rateLimiterPool.get(hashCode);
                if (!rateLimiter.tryAcquire()){
                    double rate = rateLimiter.getRate();
                    throw new QpsException(qpsLimit.qps(),rate);
                }
            } else {
                rateLimiterPool.put(hashCode, RateLimiter.create(qpsLimit.qps()));
            }
        }
    }

    @Override
    public Object after(Class clazz, Method method, Object object) {
        return object;
    }

    @Override
    public void throwable(Class clazz, Method method, Throwable throwable) {

    }
}
