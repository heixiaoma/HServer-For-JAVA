package cn.hserver.core.ioc.ref.init;

import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.annotation.Hook;
import cn.hserver.core.ioc.ref.HookCheck;
import cn.hserver.core.ioc.ref.PackageScanner;
import cn.hserver.core.server.util.ClassLoadUtil;
import cn.hserver.core.server.util.HookProxyFactory;
import javassist.util.proxy.ProxyObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InitHook extends Init {


    public InitHook(Set<String> packages) {
        super(packages);
    }


    private HookCheck checkHook(Class aClass1) {
        /**
         * 检查IOC是否存在aClass1对象
         * 遍历IOC,取出名字，将其替换为代理对象
         * Ioc存在单个Bean 和List集合类型
         */
        String iocName = null;
        boolean isList = false;
        Map<String, Object> allIoc = IocUtil.getAll();
        Iterator<String> iterator = allIoc.keySet().iterator();
        while (iterator.hasNext()) {
            String tempIocName = iterator.next();
            Object v = allIoc.get(tempIocName);
            if (v instanceof List) {
                List v1 = (List) v;
                for (Object o : v1) {
                    if (aClass1.isAssignableFrom(o.getClass())) {
                        iocName = tempIocName;
                        isList = true;
                        break;
                    }
                }
            } else {
                if (aClass1.isAssignableFrom(v.getClass()) && !ProxyObject.class.isAssignableFrom(v.getClass())) {
                    iocName = tempIocName;
                    break;
                }
            }
        }
        if (iocName == null) {
            return null;
        }
        return new HookCheck(iocName, isList);
    }

    @Override
    public void init(PackageScanner scan) throws Exception {
        HookProxyFactory hookProxyFactory = new HookProxyFactory();
        Set<Class<?>> clasps = scan.getAnnotationList(Hook.class);
        for (Class aClass : clasps) {
            Hook hook = (Hook) aClass.getAnnotation(Hook.class);
            Class[] values = hook.value();
            for (Class value : values) {
                //Hook的是类注解.
                if (Annotation.class.isAssignableFrom(value)) {
                    for (String aPackage : packages) {
                        List<Class<?>> classes = ClassLoadUtil.LoadClasses(aPackage, false);
                        for (Class<?> aClass1 : classes) {
                            try {
                                Annotation annotation = aClass1.getAnnotation(value);
                                //看看类上面的注解是否有，
                                if (annotation != null) {
                                    Object newProxyInstance = hookProxyFactory.newProxyInstance(aClass1, value.getName() + "HOOK");
                                    if (newProxyInstance != null) {
                                        //不同名字，同样的bean 在引用不会出错
                                        IocUtil.addBean(newProxyInstance.getClass().getName(), newProxyInstance);
                                        HookCheck hookCheck = checkHook(aClass1);
                                        //说明是容器存在的，使用后将其替换
                                        if (hookCheck != null) {
                                            if (hookCheck.isList()) {
                                                IocUtil.addListBean(hookCheck.getIocName(), newProxyInstance);
                                            } else {
                                                IocUtil.addBean(hookCheck.getIocName(), newProxyInstance);
                                            }
                                        } else {
                                            IocUtil.addBean(aClass1.getName(), newProxyInstance);
                                        }
                                    }
                                } else {
                                    //方法级别的调用
                                    Method[] declaredMethods = aClass1.getDeclaredMethods();
                                    for (Method declaredMethod : declaredMethods) {
                                        Annotation annotation1 = declaredMethod.getAnnotation(value);
                                        if (annotation1 != null) {
                                            Object newProxyInstance = hookProxyFactory.newProxyInstance(aClass1, value.getName() + "HOOK");
                                            if (newProxyInstance != null) {
                                                IocUtil.addBean(newProxyInstance.getClass().getName(), newProxyInstance);
                                                HookCheck hookCheck = checkHook(aClass1);
                                                //说明是容器存在的，使用后将其替换
                                                if (hookCheck != null) {
                                                    if (hookCheck.isList()) {
                                                        IocUtil.addListBean(hookCheck.getIocName(), newProxyInstance);
                                                    } else {
                                                        IocUtil.addBean(hookCheck.getIocName(), newProxyInstance);
                                                    }
                                                } else {
                                                    IocUtil.addBean(aClass1.getName(), newProxyInstance);
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                            } catch (Throwable ignored) {
                            }
                        }
                    }
                } else {
                    //hook的普通方法
                    //检查容器是否有，没有重0生产 ,有就基于现在的进行生产
                    Object bean = IocUtil.getBean(value);
                    Object newProxyInstance;
                    if (bean != null) {
                        Class<?> aClass1 = bean.getClass();
                        newProxyInstance = hookProxyFactory.newProxyInstance(aClass1, value.getName() + "HOOK");
                    } else {
                        newProxyInstance = hookProxyFactory.newProxyInstance(value, value.getName() + "HOOK");
                    }
                    //将代理类放入容器，,一会让注入的时候就是代理类注入进去了
                    if (newProxyInstance != null) {
                        IocUtil.addBean(newProxyInstance.getClass().getName(), newProxyInstance);
                        IocUtil.addBean(value.getName(), newProxyInstance);
                    }
                }
                IocUtil.addListBean(value.getName() + "HOOK", aClass.newInstance());
            }
        }
    }
}
