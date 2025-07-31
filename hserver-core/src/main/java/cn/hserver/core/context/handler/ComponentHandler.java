package cn.hserver.core.context.handler;

import cn.hserver.core.ioc.annotation.Component;
import cn.hserver.core.ioc.annotation.Scope;
import cn.hserver.core.ioc.bean.BeanDefinition;
import cn.hserver.core.scheduling.TaskManager;
import cn.hserver.core.scheduling.annotation.Task;
import cn.hserver.core.scheduling.bean.TaskDefinition;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComponentHandler implements AnnotationHandler {
    @Override
    public void handle(Class<?> clazz, Map<String, BeanDefinition> beanDefinitions) {
        String className = clazz.getName();
        if (clazz.isAnnotationPresent(Component.class)) {
            Component component = clazz.getAnnotation(Component.class);
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setBeanClass(clazz);
            String beanName = component.value();
            if (beanName.isEmpty()) {
                beanName = beanDefinition.getDefaultBeanName();
            }
            // 处理作用域
            if (clazz.isAnnotationPresent(Scope.class)) {
                Scope scope = clazz.getAnnotation(Scope.class);
                beanDefinition.setScope(scope.value());
            }
            beanDefinitions.put(beanName, beanDefinition);
            // 处理task
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                if (declaredMethod.isAnnotationPresent(Task.class)) {
                    Task task = declaredMethod.getAnnotation(Task.class);
                    TaskManager.addTask(new TaskDefinition(task.name(),task.time(),beanName,declaredMethod));
                }
            }
        }
    }
}
