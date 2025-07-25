package cn.hserver.core.ioc;

import cn.hserver.core.ioc.context.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) throws Exception {
        // 创建应用上下文，扫描指定包
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("cn.hserver.core.ioc");
        
        // 获取 Bean
        ServiceA serviceA = context.getBean(ServiceA.class);
        
        // 使用 Bean
        System.out.println(serviceA.doSomething());
    }
}    