package cn.hserver.core.task;

import cn.hserver.core.interfaces.TaskJob;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * @author hxm
 */
public interface CronExecutorService extends ExecutorService {

    ScheduledFuture submit(CronExpression expression, String className, Method method, Object... args);
    ScheduledFuture submit(Integer expression, String className, Method method, Object... args);
    ScheduledFuture submit(CronExpression expression, TaskJob taskJob, Object... args);
    ScheduledFuture submit(Integer expression, TaskJob taskJob, Object... args);

}