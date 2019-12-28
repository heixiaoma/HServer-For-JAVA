package top.hserver.cloud.proxy;

import java.lang.reflect.Method;

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
        proxyFactory.setSuperclass(clazz);
        proxyFactory.setHandler(new MethodHandler() {
            public Object invoke(Object self, Method thismethod, Method proceed, Object[] args) throws Throwable {
                //这里实现远程调用啦！
                log.info("----------------远程调用开始----------------");
                InvokeServiceData invokeServiceData = new InvokeServiceData();
                invokeServiceData.setMethod(thismethod.getName());
                invokeServiceData.setAClass(clazz.getName());
                invokeServiceData.setObjects(args);
                ServerHandler.SendInvoker(invokeServiceData);
                log.info("----------------远程调用结束----------------");
                return "result";
            }
        });
        return proxyFactory.createClass().newInstance();
    }
}
