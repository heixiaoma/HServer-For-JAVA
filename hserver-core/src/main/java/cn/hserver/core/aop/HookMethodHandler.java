package cn.hserver.core.aop;

import cn.hserver.core.aop.bean.HookBeanDefinition;
import cn.hserver.core.context.AnnotationConfigApplicationContext;
import javassist.util.proxy.MethodHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HookMethodHandler implements MethodHandler {

    private final HookBeanDefinition hookBeanDefinition;
    private HookAdapter hookAdapter;

    public HookMethodHandler(HookBeanDefinition hookBeanDefinition) {
        this.hookBeanDefinition = hookBeanDefinition;
    }

    @Override
    public Object invoke(Object self, Method thismethod, Method proceed, Object[] args) throws Throwable {
        if (hookAdapter==null){
            hookAdapter = (HookAdapter) AnnotationConfigApplicationContext.getBean(hookBeanDefinition.getHookHandler());
        }
        for (Method method : hookBeanDefinition.getHookMethod()) {
            if (method.equals(thismethod)) {
                try {
                    hookAdapter.before(self.getClass(), thismethod, args);
                    proceed.setAccessible(true);
                    Object result = proceed.invoke(self, args);
                    result = hookAdapter.after(self.getClass(), thismethod, result);
                    return result;
                }catch (InvocationTargetException ite) {
                    Throwable targetException = ite.getTargetException();
                    hookAdapter.throwable(self.getClass(), thismethod, targetException);
                    throw new RuntimeException("方法执行失败", targetException);
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    // 处理反射调用异常
                    hookAdapter.throwable(self.getClass(), thismethod, e);
                    throw new RuntimeException("反射调用失败", e);
                } catch (Throwable t) {
                    // 处理其他异常
                    hookAdapter.throwable(self.getClass(), thismethod, t);
                    throw new RuntimeException("钩子处理过程中发生未知异常", t);
                }
            }
        }
        return proceed.invoke(self, args);
    }
}
