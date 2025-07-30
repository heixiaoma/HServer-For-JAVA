package cn.hserver.core.aop;

import cn.hserver.core.aop.annotation.Hook;
import cn.hserver.core.aop.bean.HookBeanDefinition;
import cn.hserver.core.ioc.bean.BeanDefinition;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HookFactory {

    private final ProxyFactory proxyFactory = new ProxyFactory();

    public void handlerHookData( Map<String, BeanDefinition> beanDefinitions){
        for (String beanName : beanDefinitions.keySet()) {
            BeanDefinition beanDefinition = beanDefinitions.get(beanName);
            if (beanDefinition.isSingleton()) {
                Hook annotation = beanDefinition.getBeanClass().getAnnotation(Hook.class);
                if (annotation != null) {
                    Class<?>[] value = annotation.value();
                    if (value != null) {
                        beanDefinitions.forEach( (k, v) -> {
                            for (Class<?> aClass : value) {
                                Method[] declaredMethods = v.getBeanClass().getDeclaredMethods();
                                //hook整个bean对象
                                if (aClass.equals(v.getBeanClass())){
                                    v.setHookBeanDefinition(new HookBeanDefinition(beanDefinition.getBeanClass(),declaredMethods));
                                    continue;
                                }
                                //按方法名hook
                                List<Method> methods = new ArrayList<>();
                                for (Method declaredMethod : declaredMethods) {
                                    Annotation[] annotations = declaredMethod.getAnnotations();
                                    for (Annotation annotation1 : annotations) {
                                        if (annotation1.annotationType().equals(aClass)) {
                                            methods.add(declaredMethod);
                                        }
                                    }
                                }
                                if (!methods.isEmpty()) {
                                    v.setHookBeanDefinition(new HookBeanDefinition(beanDefinition.getBeanClass(), methods.toArray(new Method[0])));
                                    methods.clear();
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    public  Object newProxyInstance(BeanDefinition beanDefinition, Constructor<?> constructor, Object[] args) throws NoClassDefFoundError,InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Class<?> hookHandler = beanDefinition.getHookBeanDefinition().getHookHandler();
        if (HookAdapter.class.isAssignableFrom(hookHandler)) {
                // 设置需要创建子类的父类
                if (ProxyObject.class.isAssignableFrom(beanDefinition.getBeanClass())) {
                    return null;
                }
                proxyFactory.setSuperclass(beanDefinition.getBeanClass());
                Object o = proxyFactory.create(constructor.getParameterTypes(), args);
                ((ProxyObject) o).setHandler(new HookMethodHandler(beanDefinition.getHookBeanDefinition()));
                return o;
            }else {
                throw new NoClassDefFoundError(hookHandler.getName()+" 未实现 HookAdapter 接口");
            }
    }

}
