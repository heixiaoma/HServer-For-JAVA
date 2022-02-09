
package top.hserver.core.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hserver.core.interfaces.TaskJob;
import top.hserver.core.ioc.IocUtil;
import top.hserver.core.server.util.ExceptionUtil;
import top.hserver.core.server.util.SslContextUtil;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.*;

import static top.hserver.core.task.TaskManager.IS_OK;

public class  ScheduledThreadPoolExecutorPro extends ScheduledThreadPoolExecutor implements CronExecutorService {
    private static final Logger log = LoggerFactory.getLogger(ScheduledThreadPoolExecutorPro.class);

    ScheduledThreadPoolExecutorPro(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
    }

    @Override
    public ScheduledFuture submit(CronExpression expression, String className, Method method, Object... args) {
        Runnable scheduleTask = () -> {
            Date now  = new Date();
            Date time = expression.getNextValidTimeAfter(now);
            try {
                while (time != null) {
                    while (now.before(time)) {
                        Thread.sleep(time.getTime() - now.getTime());
                        now = new Date();
                    }
                    time = expression.getNextValidTimeAfter(now);
                    if (IS_OK) {
                        try {
                            method.setAccessible(true);
                            method.invoke(IocUtil.getBean(className), args);
                        } catch (Exception e) {
                            log.error(ExceptionUtil.getMessage(e));
                        }
                    }
                }
            } catch (RejectedExecutionException | CancellationException ignored) {
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        return this.schedule(scheduleTask, 1, TimeUnit.MILLISECONDS);
    }

    @Override
    public ScheduledFuture submit(CronExpression expression, TaskJob taskJob, Object... args) {
        Runnable scheduleTask = () -> {
            Date now  = new Date();
            Date time = expression.getNextValidTimeAfter(now);
            try {
                while (time != null) {
                    while (now.before(time)) {
                        Thread.sleep(time.getTime() - now.getTime());
                        now = new Date();
                    }
                    time = expression.getNextValidTimeAfter(now);
                    if (IS_OK) {
                        try {
                            taskJob.exec(args);
                        } catch (Exception e) {
                            log.error(ExceptionUtil.getMessage(e));
                        }
                    }
                }
            } catch (RejectedExecutionException | CancellationException ignored) {
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        return this.schedule(scheduleTask, 1, TimeUnit.MILLISECONDS);
    }

    @Override
    public ScheduledFuture submit(Integer expression, String className, Method method, Object... args) {
        Runnable scheduleTask = () -> {
                if (IS_OK) {
                    try {
                        method.setAccessible(true);
                        method.invoke(IocUtil.getBean(className), args);
                    } catch (Exception e) {
                        log.error(ExceptionUtil.getMessage(e));
                    }
                }
        };
        return this.scheduleAtFixedRate(scheduleTask,expression,expression, TimeUnit.MILLISECONDS);
    }

    @Override
    public ScheduledFuture submit(Integer expression, TaskJob taskJob, Object... args) {
        Runnable scheduleTask = () -> {
            if (IS_OK) {
                try {
                    taskJob.exec(args);
                } catch (Exception e) {
                    log.error(ExceptionUtil.getMessage(e));
                }
            }
        };
        return this.scheduleAtFixedRate(scheduleTask, expression,expression, TimeUnit.MILLISECONDS);
    }
}