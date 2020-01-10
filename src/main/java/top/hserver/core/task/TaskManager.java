package top.hserver.core.task;

import lombok.extern.slf4j.Slf4j;
import top.hserver.core.interfaces.TaskJob;
import top.hserver.core.server.epoll.NamedThreadFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class TaskManager {

    public static Boolean IS_OK = false;

    private static final Map<String, ScheduledFuture<?>> cronTask = new ConcurrentHashMap<>();

    private static final ScheduledThreadPoolExecutorPro scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutorPro(Runtime.getRuntime().availableProcessors()+1, new NamedThreadFactory("hserver_task@"));

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
            if (cronTask.containsKey(name)) {
                log.warn("{}任务名已经存在",name);
                return;
            }
            TaskJob taskJob1 = taskJob.newInstance();
            //毫秒级默认
            try {
                //cron定时器
                CronExpression expression = new CronExpression(time);
                ScheduledFuture<?> submit = scheduledThreadPoolExecutor.submit(expression, taskJob1, args);
                cronTask.put(name, submit);
            } catch (Exception e) {
                try {
                    int times = Integer.parseInt(time);
                    ScheduledFuture<?> submit = scheduledThreadPoolExecutor.submit(times, taskJob1, args);
                    cronTask.put(name, submit);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除任务
     */
    public static boolean removeTask(String name) {
        ScheduledFuture<?> scheduledFuture = cronTask.get(name);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            cronTask.remove(name);
            return true;
        }
        return false;
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
        if (cronTask.containsKey(name)) {
            log.warn("{}任务名已经存在",name);
            return;
        }
        try {
            //cron定时器
            CronExpression expression = new CronExpression(time);
            ScheduledFuture<?> submit = scheduledThreadPoolExecutor.submit(expression, className, method, args);
            cronTask.put(name, submit);
        } catch (Exception e) {
            try {
                //毫秒级定时器
                int times = Integer.parseInt(time);
                ScheduledFuture<?> submit = scheduledThreadPoolExecutor.submit(times, className, method, args);
                cronTask.put(name, submit);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
    }

}
