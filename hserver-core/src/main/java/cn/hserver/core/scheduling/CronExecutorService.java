package cn.hserver.core.scheduling;


import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * @author hxm
 */
public interface CronExecutorService extends ExecutorService {

    ScheduledFuture<?> submit(CronExpression expression, String className, Method method);
    ScheduledFuture<?> submit(Integer expression, String className, Method method);
    ScheduledFuture<?> submit(CronExpression expression, TaskJob taskJob, Object... args);
    ScheduledFuture<?> submit(Integer expression, TaskJob taskJob, Object... args);

    default Object[] getNullParm(Method method){
        int parameterCount = method.getParameterCount();
        return new Object[parameterCount];
    }
}