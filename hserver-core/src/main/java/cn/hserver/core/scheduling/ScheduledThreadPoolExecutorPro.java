
package cn.hserver.core.scheduling;

import cn.hserver.core.context.IocApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.*;


public class  ScheduledThreadPoolExecutorPro extends ScheduledThreadPoolExecutor implements CronExecutorService {
    private static final Logger log = LoggerFactory.getLogger(ScheduledThreadPoolExecutorPro.class);

    ScheduledThreadPoolExecutorPro(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
    }

    @Override
    public ScheduledFuture<?> submit(CronExpression expression, String beanName, Method method) {
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
                    try {
                        method.setAccessible(true);
                        Object bean = IocApplicationContext.getBean(beanName);
                        if (bean!=null) {
                            method.invoke(bean,getNullParm(method));
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(),e);
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
    public ScheduledFuture<?> submit(Integer expression, String beanName, Method method) {
        Runnable scheduleTask = () -> {
            try {
                method.setAccessible(true);
                Object bean = IocApplicationContext.getBean(beanName);
                if (bean!=null) {
                    method.invoke(bean,getNullParm(method));
                }
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }
        };
        return this.scheduleAtFixedRate(scheduleTask,expression,expression, TimeUnit.MILLISECONDS);
    }



    @Override
    public ScheduledFuture<?> submit(CronExpression expression, TaskJob taskJob, Object... args) {
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
                    try {
                        taskJob.exec(args);
                    } catch (Exception e) {
                        log.error(e.getMessage(),e);
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
    public ScheduledFuture<?> submit(Integer expression, TaskJob taskJob, Object... args) {
        Runnable scheduleTask = () -> {
            try {
                taskJob.exec(args);
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }
        };
        return this.scheduleAtFixedRate(scheduleTask, expression,expression, TimeUnit.MILLISECONDS);
    }
}
