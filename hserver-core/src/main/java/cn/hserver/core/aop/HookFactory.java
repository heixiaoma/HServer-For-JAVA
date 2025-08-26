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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HookFactory {


    public static void handlerHookData( Map<String, BeanDefinition> beanDefinitions){
        for (String beanName : beanDefinitions.keySet()) {
            BeanDefinition beanDefinition = beanDefinitions.get(beanName);
            if (beanDefinition.isSingleton()) {
                Hook annotation = beanDefinition.getBeanClass().getAnnotation(Hook.class);
                if (annotation != null) {
                    //可能是注解或者容器bean实例
                    Class<?>[] value = annotation.value();
                    if (value != null) {
                        beanDefinitions.forEach( (k, v) -> {
                           a:for (Class<?> aClass : value) {
                                Method[] declaredMethods = v.getBeanClass().getDeclaredMethods();
                                //hook整个bean对象
                                if (aClass.equals(v.getBeanClass())) {
                                    v.addHookBeanDefinition(new HookBeanDefinition(beanDefinition.getBeanClass(),declaredMethods));
                                    continue;
                                }
                                //hook类上的注解
                                Annotation[] classAnnotations = v.getBeanClass().getAnnotations();
                               for (Annotation classAnnotation : classAnnotations) {
                                    if (classAnnotation.annotationType().equals(aClass)) {
                                        v.addHookBeanDefinition(new HookBeanDefinition(beanDefinition.getBeanClass(),declaredMethods));
                                        continue a;
                                    }
                                }
                                //按注解方法名hook
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
                                    v.addHookBeanDefinition(new HookBeanDefinition(beanDefinition.getBeanClass(), methods.toArray(new Method[0])));
                                    methods.clear();
                                }
                            }
                        });
                    }
                }
            }
        }
    }


}
