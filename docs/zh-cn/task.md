
## 定时器

```java
@Bean
public class TaskTest {
    
    @Autowired
    private TestService testService;

    private boolean flag = true;

    public void dynamicAddTimer() {
        System.out.println("动态添加定时任务");
        TaskManager.addTask("测试任务2", "2000", TestTask.class,"666");
    }
    
    
    @Task(name = "测试定时任务1", time ="*/5 * * * * ?")
    public void timerTask() {
        System.out.println("测试定时任务，注入的对象调用结果:" + testService.testa());
        if (flag) {
            dynamicAddTimer();
            flag = false;
        }
    }

    @Task(name = "测试定时任务2", time = "2000")
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

//动态添加定时任务的实现类必须要实现一个TaskJob,样才能被TaskManager管理
//添加任务 TaskManager.addTask("测试任务2", "2000", TestTask.class,"666");
//删除任务  boolean is_success = TaskManager.removeTask("测试任务2");
public class TestTask implements TaskJob {

    @Override
    public void exec(Object... args) {
        String args_ = "";
        for (Object arg : args) {
            args_ += arg.toString();
        }
        System.out.println("测试定时器动态添加任务，参数是：" + args_);
    }
}
```
