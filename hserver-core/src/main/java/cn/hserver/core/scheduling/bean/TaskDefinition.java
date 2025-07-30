package cn.hserver.core.scheduling.bean;

import java.lang.reflect.Method;

public class TaskDefinition {
    private final String name;
    private final String time;
    private final String beanName;
    private final Method method;

    public TaskDefinition(String name,String time,String beanName,Method method) {
        this.name=name;
        this.time=time;
        this.beanName=beanName;
        this.method=method;
    }

    public Method getMethod() {
        return method;
    }

    public String getBeanName() {
        return beanName;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }
}
