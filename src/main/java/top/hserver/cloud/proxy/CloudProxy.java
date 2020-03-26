package top.hserver.cloud.proxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.bean.InvokeServiceData;
import top.hserver.cloud.server.handler.ServerHandler;
import top.hserver.core.ioc.annotation.Resource;

@Slf4j
public class CloudProxy {

    private static Map<String, Object> IOC = new HashMap<>();


    @SuppressWarnings("deprecation")
    public static Object getProxy(Class clazz, Resource resource) throws InstantiationException, IllegalAccessException {

        String value = resource.value();
        if (value.trim().length() > 0) {
            Object o = IOC.get(value);
            if (o != null) {
                return o;
            }
        }
        Object o = IOC.get(clazz.getName());
        if (o != null) {
            return o;
        }
        // 代理工厂
        ProxyFactory proxyFactory = new ProxyFactory();
        // 设置需要创建子类的父类
        if (clazz.isInterface()) {
            proxyFactory.setInterfaces(new Class[]{clazz});
        } else {
            proxyFactory.setSuperclass(clazz);
        }
        proxyFactory.setHandler(new MethodHandler() {
            @Override
            public Object invoke(Object self, Method thismethod, Method proceed, Object[] args) throws Throwable {
                //这里实现远程调用啦！
                InvokeServiceData invokeServiceData = new InvokeServiceData();
                invokeServiceData.setMethod(thismethod.getName());
                if (resource.value().trim().length() > 0) {
                    invokeServiceData.setAClass(resource.value());
                } else {
                    invokeServiceData.setAClass(clazz.getName());
                }
                invokeServiceData.setObjects(args);
                invokeServiceData.setUUID(UUID.randomUUID().toString());
                return ServerHandler.SendInvoker(invokeServiceData);
            }
        });

        Object o1 = proxyFactory.createClass().newInstance();
        if (value.trim().length() > 0) {
            IOC.put(value, o1);
        } else {
            IOC.put(clazz.getName(), o1);
        }
        return o1;
    }

    public static void clearCache() {
        IOC.clear();
    }
}
