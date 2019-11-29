package com.hserver.core.task;

import com.hserver.core.bean.TaskBean;
import com.hserver.core.interfaces.TaskJob;
import com.hserver.core.ioc.Ioc;
import com.hserver.core.ioc.IocUtil;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;


public class TaskManager {

    public static Boolean IS_OK = false;
    private static final Map<String, TaskBean> nameTask = new ConcurrentHashMap<>();


    /**
     * 动态添加任务
     * @param name
     * @param time
     * @param taskJob
     * @param args
     */
    public static void addTask(String name, Integer time, Class<? extends TaskJob> taskJob, Object... args) {
        try {
            TaskJob taskJob1 = taskJob.newInstance();
            Timer sysTimer = new Timer(name);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (IS_OK) {
                        try {
                            taskJob1.exec(args);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            nameTask.put(name, new TaskBean(sysTimer, timerTask));
            sysTimer.schedule(timerTask, time, time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除任务
     */
    public static boolean removeTask(String name) {
        TaskBean taskBean = nameTask.get(name);
        if (taskBean != null) {
            Timer timer = taskBean.getTimer();
            TimerTask timerTask = taskBean.getTimerTask();
            timerTask.cancel();
            timer.cancel();
            timer.purge();
            nameTask.remove(name);
            return true;
        }
        return false;
    }

    /**
     * 系统自己用的
     * @param name
     * @param time
     * @param className
     * @param method
     * @param args
     */
    public static void initTask(String name, Integer time, String className, Method method, Object... args) {
        try {
            Timer sysTimer = new Timer(name);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (IS_OK) {
                        try {
                            Object bean = IocUtil.getBean(className);
                            method.invoke(bean, args);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            nameTask.put(name, new TaskBean(sysTimer, timerTask));
            sysTimer.schedule(timerTask, time, time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
