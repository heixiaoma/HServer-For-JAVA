package cn.hserver.core.aop;

import cn.hserver.core.aop.bean.HookBeanDefinition;
import cn.hserver.core.context.IocApplicationContext;
import cn.hserver.core.ioc.annotation.Order;
import javassist.util.proxy.MethodHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HookMethodHandler implements MethodHandler {

    private final List<HookBeanDefinition> hookBeanDefinitions;

    public HookMethodHandler(List<HookBeanDefinition> hookBeanDefinitions) {
        this.hookBeanDefinitions = sortByOrder(hookBeanDefinitions);
    }

    private List<HookBeanDefinition>sortByOrder(List<HookBeanDefinition> beans) {
        return beans.stream()
                .sorted((bean1, bean2) -> {
                    int order1 = getOrderValue(bean1.getHookHandler());
                    int order2 = getOrderValue(bean2.getHookHandler());
                    return Integer.compare(order1, order2);
                })
                .collect(Collectors.toList());
    }

    // 获取类上@Order注解的值，默认值为Integer.MAX_VALUE（最低优先级）
    private int getOrderValue(Class<?> clazz) {
        Order orderAnnotation = clazz.getAnnotation(Order.class);
        return orderAnnotation != null ? orderAnnotation.value() : Integer.MAX_VALUE;
    }



    @Override
    public Object invoke(Object self, Method thismethod, Method proceed, Object[] args) throws Throwable {
        if (hookBeanDefinitions.isEmpty()){
            return proceed.invoke(self, args);
        }
        try {
            //执行前
            for (HookBeanDefinition hookBeanDefinition : hookBeanDefinitions) {
                if (hookBeanDefinition.isMatchMethod(thismethod)) {
                    HookAdapter hookAdapter = hookBeanDefinition.getHookAdapter();
                    hookAdapter.before(self.getClass(), thismethod, args);
                }
            }
            //执行方法
            proceed.setAccessible(true);
            Object result = proceed.invoke(self, args);

            //执行后
            for (HookBeanDefinition hookBeanDefinition : hookBeanDefinitions) {
                if (hookBeanDefinition.isMatchMethod(thismethod)) {
                    HookAdapter hookAdapter = hookBeanDefinition.getHookAdapter();
                    result = hookAdapter.after(self.getClass(), thismethod, result);
                }
            }
            return result;
        }catch (InvocationTargetException ite) {
            Throwable targetException = ite.getTargetException();
            for (HookBeanDefinition hookBeanDefinition : hookBeanDefinitions) {
                if (hookBeanDefinition.isMatchMethod(thismethod)) {
                    HookAdapter hookAdapter = hookBeanDefinition.getHookAdapter();
                   hookAdapter.throwable(self.getClass(), thismethod, targetException);
                }
            }
            throw new RuntimeException("方法执行失败", targetException);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            // 处理反射调用异常
            for (HookBeanDefinition hookBeanDefinition : hookBeanDefinitions) {
                if (hookBeanDefinition.isMatchMethod(thismethod)) {
                    HookAdapter hookAdapter = hookBeanDefinition.getHookAdapter();
                    hookAdapter.throwable(self.getClass(), thismethod, e);
                }
            }
            throw new RuntimeException("反射调用失败", e);
        } catch (Throwable t) {
            // 处理其他异常
            for (HookBeanDefinition hookBeanDefinition : hookBeanDefinitions) {
                if (hookBeanDefinition.isMatchMethod(thismethod)) {
                    HookAdapter hookAdapter = hookBeanDefinition.getHookAdapter();
                    hookAdapter.throwable(self.getClass(), thismethod, t);
                }
            }
            throw new RuntimeException("钩子处理过程中发生未知异常", t);
        }
    }
}
