package top.hserver.cloud.proxy;

import java.lang.reflect.Method;
import java.util.UUID;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.bean.InvokeServiceData;
import top.hserver.cloud.server.handler.ServerHandler;

@Slf4j
public class CloudProxy {
    @SuppressWarnings("deprecation")
    public static Object getProxy(Class clazz) throws InstantiationException, IllegalAccessException {
        // 代理工厂
        ProxyFactory proxyFactory = new ProxyFactory();
        // 设置需要创建子类的父类
        if (clazz.isInterface()) {
            proxyFactory.setInterfaces(new Class[]{clazz});
        } else {
            proxyFactory.setSuperclass(clazz);
        }
        proxyFactory.setHandler(new MethodHandler() {
            public Object invoke(Object self, Method thismethod, Method proceed, Object[] args) throws Throwable {
                //这里实现远程调用啦！
                InvokeServiceData invokeServiceData = new InvokeServiceData();
                invokeServiceData.setMethod(thismethod.getName());
                invokeServiceData.setAClass(clazz.getName());
                invokeServiceData.setObjects(args);
                invokeServiceData.setUUID(UUID.randomUUID().toString());
                return ServerHandler.SendInvoker(invokeServiceData);
            }
        });
        return proxyFactory.createClass().newInstance();
    }
}
