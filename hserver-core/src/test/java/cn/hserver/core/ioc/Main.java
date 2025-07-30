package cn.hserver.core.ioc;


import cn.hserver.core.context.AnnotationConfigApplicationContext;
import cn.hserver.core.scheduling.TaskManager;

public class Main {
    public static void main(String[] args) throws Exception {
        // 创建应用上下文，扫描指定包
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("cn.hserver.core.ioc");
        TaskManager.startTask();
        Thread.sleep(100000);
    }
}
