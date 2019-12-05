package top.test.task;

import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.ioc.annotation.Task;
import top.hserver.core.task.TaskManager;
import top.test.service.TestService;

@Bean
public class TaskTest {

    @Autowired
    private TestService testService;

    private boolean flag = true;

    public void dynamicAddTimer() {
        System.out.println("动态添加定时任务");
        TaskManager.addTask("测试任务2", 1000 * 2, TestTask.class,"666");
    }

    @Task(name = "测试定时任务1", time = 1000 * 2)
    public void timerTask() {
        System.out.println("测试定时任务，注入的对象调用结果:" + testService.testa());
        if (flag) {
            dynamicAddTimer();
            flag = false;
        }
    }

    @Task(name = "测试定时任务2", time = 1000 * 10)
    public void removeTask() {
        //干掉方法注解版本
        boolean task1 = TaskManager.removeTask("测试定时任务1");
        //干掉动态添加的
        boolean task2 = TaskManager.removeTask("测试任务2");
        //干掉自己
        boolean task3 = TaskManager.removeTask("测试定时任务2");
        //结果
        System.out.println("任务已经被干掉了 tash1=" + task1 + ",task2=" + task2 + ",task3=" + task3);
    }

}
