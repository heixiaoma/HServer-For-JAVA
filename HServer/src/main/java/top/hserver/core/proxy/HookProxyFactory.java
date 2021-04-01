package top.hserver.core.proxy;

import javassist.util.proxy.ProxyObject;
import top.hserver.core.interfaces.HookAdapter;
import top.hserver.core.ioc.IocUtil;
import javassist.util.proxy.ProxyFactory;
import top.hserver.core.ioc.annotation.Hook;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author hxm
 */
public class HookProxyFactory {

    public Object newProxyInstance(Class clazz, String hookPageName) throws InstantiationException, IllegalAccessException {
        // 代理工厂
        ProxyFactory proxyFactory = new ProxyFactory();
        // 设置需要创建子类的父类
        if (ProxyObject.class.isAssignableFrom(clazz)) {
            return null;
        }
        proxyFactory.setSuperclass(clazz);
        Object o = proxyFactory.createClass().newInstance();
        ((ProxyObject) o).setHandler((self, thismethod, proceed, args) -> {
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                if (declaredMethod.getName().equals(thismethod.getName())) {
                    List<HookAdapter> listBean = (List) IocUtil.getListBean(hookPageName);
                    for (HookAdapter hookAdapter : listBean) {
                        if (check(hookAdapter, self.getClass(), thismethod)) {
                            hookAdapter.before(self.getClass(), thismethod, args);
                        }
                    }
                    try {
                        Object result = proceed.invoke(self, args);
                        for (HookAdapter hookAdapter : listBean) {
                            if (check(hookAdapter, self.getClass(), thismethod)) {
                                result = hookAdapter.after(self.getClass(), thismethod, result);
                            }
                        }
                        return result;
                    } catch (Throwable e) {
                        for (HookAdapter hookAdapter : listBean) {
                            if (check(hookAdapter, self.getClass(), thismethod)) {
                                if (e instanceof InvocationTargetException) {
                                    e = ((InvocationTargetException) e).getTargetException();
                                }
                                hookAdapter.throwable(self.getClass(), thismethod, e);
                            }
                        }
                        if (e instanceof InvocationTargetException) {
                            throw ((InvocationTargetException) e).getTargetException();
                        }
                        throw e;
                    }
                }
            }
            return proceed.invoke(self, args);
        });
        return o;
    }


    private boolean check(HookAdapter hookAdapter, Class self, Method method) {
        Hook hook = hookAdapter.getClass().getAnnotation(Hook.class);
        for (Class aClass : hook.value()) {

            //Hoook 类

            Class superclass = self.getSuperclass();
            if (aClass == superclass) {
                return true;
            }


            //hook 注解
            /**
             * 检查是否是类级别的检查
             */
            Annotation[] annotations1 = self.getSuperclass().getAnnotations();
            for (Annotation annotation : annotations1) {
                if (annotation.annotationType() == aClass) {
                    return true;
                }
            }
            /**
             * 检查是否方法级别的
             */
            Annotation[] annotations2 = method.getAnnotations();
            for (Annotation annotation : annotations2) {
                if (annotation.annotationType() == aClass) {
                    return true;
                }
            }
        }
        return false;
    }

}
