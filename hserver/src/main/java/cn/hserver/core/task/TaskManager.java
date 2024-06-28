package cn.hserver.core.task;

import cn.hserver.core.server.util.Calculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.hserver.core.interfaces.TaskJob;
import cn.hserver.core.server.context.ConstConfig;
import cn.hserver.core.server.util.ExceptionUtil;
import cn.hserver.core.server.util.NamedThreadFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author hxm
 */
public class TaskManager {
    private static final Logger log = LoggerFactory.getLogger(TaskManager.class);

    public static Boolean IS_OK = false;

    private static final Map<String, ScheduledFuture<?>> CRON_TASK = new ConcurrentHashMap<>();

    private static final ScheduledThreadPoolExecutorPro SCHEDULED_THREAD_POOL_EXECUTOR = new ScheduledThreadPoolExecutorPro(ConstConfig.taskPool, new NamedThreadFactory("hserver_task"));

    /**
     * 动态添加任务
     *
     * @param name
     * @param time
     * @param taskJob
     * @param args
     */
    public static void addTask(String name, String time, Class<? extends TaskJob> taskJob, Object... args) {

        try {
            if (CRON_TASK.containsKey(name)) {
                log.warn("{}任务名已经存在", name);
                return;
            }
            TaskJob taskJob1 = taskJob.newInstance();
            //毫秒级默认
            try {
                //cron定时器
                CronExpression expression = new CronExpression(time);
                ScheduledFuture<?> submit = SCHEDULED_THREAD_POOL_EXECUTOR.submit(expression, taskJob1, args);
                CRON_TASK.put(name, submit);
            } catch (Exception e) {
                try {
                    int times = Integer.parseInt(time);
                    ScheduledFuture<?> submit = SCHEDULED_THREAD_POOL_EXECUTOR.submit(times, taskJob1, args);
                    CRON_TASK.put(name, submit);
                } catch (Exception e2) {
                    log.error(e2.getMessage(),e2);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }

    /**
     * 获取所有任务名字，可以和自己数据表建立关系
     *
     * @return
     */
    public static Set<String> getAllTaskName() {
        return CRON_TASK.keySet();
    }

    /**
     * 删除任务
     */
    public static boolean removeTask(String name) {
        ScheduledFuture<?> scheduledFuture = CRON_TASK.get(name);
        if (scheduledFuture != null) {
            boolean cancel = scheduledFuture.cancel(true);
            if (cancel) {
                CRON_TASK.remove(name);
                return true;
            }
        }
        return false;
    }

    /**
     * 存在任务？
     *
     * @param name
     * @return
     */
    public static boolean existTask(String name) {
        return CRON_TASK.containsKey(name);
    }


    /**
     * 系统自己用的
     *
     * @param name
     * @param time
     * @param className
     * @param method
     * @param args
     */
    public static void initTask(String name, String time, String className, Method method, Object... args) {
        if (CRON_TASK.containsKey(name)) {
            log.warn("{}任务名已经存在", name);
            return;
        }
        try {
            //cron定时器
            CronExpression expression = new CronExpression(time);
            ScheduledFuture<?> submit = SCHEDULED_THREAD_POOL_EXECUTOR.submit(expression, className, method, args);
            CRON_TASK.put(name, submit);
        } catch (Exception e) {
            try {
                //毫秒级定时器
                int times = (int) Calculator.calculate(time);
                ScheduledFuture<?> submit = SCHEDULED_THREAD_POOL_EXECUTOR.submit(times, className, method, args);
                CRON_TASK.put(name, submit);
            } catch (Exception e1) {
                log.error(e1.getMessage(),e1);
            }

        }
    }

}
