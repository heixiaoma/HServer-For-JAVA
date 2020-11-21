package net.hserver.task;

import net.hserver.service.TestService;
import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.ioc.annotation.Task;
import top.hserver.core.task.TaskManager;

import java.text.SimpleDateFormat;
import java.util.Date;

@Bean
public class TaskTest {

    @Autowired
    private TestService testService;

    private boolean flag = true;

    public void dynamicAddTimer() {
        System.out.println("动态添加定时任务");
        TaskManager.addTask("测试任务2", "2000", TestTask.class, "666");
    }

    @Task(name = "测试定时任务1", time = "*/5 * * * * ?")
    public void timerTask() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        System.out.println("测试定时任务，{}，注入的对象调用结果:{}" + df.format(new Date()) + testService.testa());
        if (flag) {
            dynamicAddTimer();
            flag = false;
        }
    }

    @Task(name = "测试定时任务2", time = "10000")
    public void removeTask() {
        //干掉方法注解版本
        boolean task1 = TaskManager.removeTask("测试定时任务1");
        //干掉动态添加的
        boolean task2 = TaskManager.removeTask("测试任务2");
        //干掉自己
        boolean task3 = TaskManager.removeTask("测试定时任务2");
        //结果
        System.out.println("任务已经被干掉了 tash1={},task2={},task3={}" + task1 + task2 + task3);
    }

}
