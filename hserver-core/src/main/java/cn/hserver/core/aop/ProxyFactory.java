package cn.hserver.core.aop;

import cn.hserver.core.aop.bean.HookBeanDefinition;
import cn.hserver.core.ioc.BeanFactory;
import cn.hserver.core.ioc.bean.BeanDefinition;
import javassist.util.proxy.ProxyObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ProxyFactory {

    private static final javassist.util.proxy.ProxyFactory proxyFactory = new javassist.util.proxy.ProxyFactory();
    public  static Object newHookProxyInstance(BeanDefinition beanDefinition, Constructor<?> constructor, Object[] args) throws NoClassDefFoundError, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // 设置需要创建子类的父类
        if (ProxyObject.class.isAssignableFrom(beanDefinition.getBeanClass())) {
            return null;
        }
        for (HookBeanDefinition hookBeanDefinition : beanDefinition.getHookBeanDefinitions()) {
            if (!HookAdapter.class.isAssignableFrom(hookBeanDefinition.getHookHandler())) {
                throw new NoClassDefFoundError(hookBeanDefinition.getHookHandler().getName()+" 未实现 HookAdapter 接口");
            }
        }
        proxyFactory.setSuperclass(beanDefinition.getBeanClass());
        Object o = proxyFactory.create(constructor.getParameterTypes(), args);
        ((ProxyObject) o).setHandler(new HookMethodHandler(beanDefinition.getHookBeanDefinitions()));
        return o;
    }

    public  static Object newRefreshProxyInstance(BeanDefinition beanDefinition, Constructor<?> constructor, Object[] args) throws NoClassDefFoundError, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // 设置需要创建子类的父类
        if (ProxyObject.class.isAssignableFrom(beanDefinition.getBeanClass())) {
            return null;
        }
        proxyFactory.setSuperclass(beanDefinition.getBeanClass());
        Object o = proxyFactory.create(constructor.getParameterTypes(), args);
        ((ProxyObject) o).setHandler(new RefreshMethodHandler(beanDefinition));
        return o;
    }
}
