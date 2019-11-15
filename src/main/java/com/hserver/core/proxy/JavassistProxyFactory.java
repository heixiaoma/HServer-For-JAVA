package com.hserver.core.proxy;

import com.hserver.core.ioc.HookAdapter;
import com.hserver.core.ioc.IocUtil;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

import java.lang.reflect.Method;

public class JavassistProxyFactory {


    @SuppressWarnings("deprecation")
    public Object newProxyInstance(Class clazz, String hookPageName, String method) throws InstantiationException, IllegalAccessException {
        // 代理工厂
        ProxyFactory proxyFactory = new ProxyFactory();
        // 设置需要创建子类的父类
        proxyFactory.setSuperclass(clazz);

        proxyFactory.setHandler(new MethodHandler() {
                                    /*
                                     * self为由Javassist动态生成的代理类实例，
                                     *  thismethod为 当前要调用的方法
                                     *  proceed 为生成的代理类对方法的代理引用。
                                     *  Object[]为参数值列表，
                                     * 返回：从代理实例的方法调用返回的值。
                                     *
                                     * 其中，proceed.invoke(self, args);
                                     *
                                     * 调用代理类实例上的代理方法的父类方法（即实体类ConcreteClassNoInterface中对应的方法）
                                     */
                                    public Object invoke(Object self, Method thismethod, Method proceed, Object[] args) throws Throwable {
                                        System.out.println("-------------proxy-------------------");
                                        HookAdapter hookAdapter = (HookAdapter) IocUtil.getBean(hookPageName);
                                        if (thismethod.getName().equals(method)) {
                                            hookAdapter.before(args);
                                        }
                                        Object result = proceed.invoke(self, args);
                                        if (thismethod.getName().equals(method)) {
                                            result = hookAdapter.after(result);
                                        }
                                        return result;
                                    }
                                }
        );
        // 通过字节码技术动态创建子类实例
        return proxyFactory.createClass().newInstance();
    }

}
