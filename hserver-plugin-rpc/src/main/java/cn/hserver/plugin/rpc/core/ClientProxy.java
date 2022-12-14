package cn.hserver.plugin.rpc.core;

import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import cn.hserver.plugin.rpc.annotation.Resource;
import cn.hserver.plugin.rpc.client.RpcClient;
import cn.hserver.plugin.rpc.codec.InvokeServiceData;

import java.util.UUID;

public class
ClientProxy {

    public ClientProxy() {
    }

    public static Object getProxy(Class clazz, Resource resource) throws InstantiationException, IllegalAccessException {
        ProxyFactory proxyFactory = new ProxyFactory();
        if (clazz.isInterface()) {
            proxyFactory.setInterfaces(new Class[]{clazz});
        } else {
            proxyFactory.setSuperclass(clazz);
        }
        Object o1 = proxyFactory.createClass().newInstance();
        ((ProxyObject) o1).setHandler((self, thisMethod, proceed, args) -> {
            InvokeServiceData invokeServiceData = new InvokeServiceData();
            invokeServiceData.setMethod(thisMethod.getName());
            invokeServiceData.setParameterTypes(thisMethod.getParameterTypes());
            if (resource.value().trim().length() > 0) {
                invokeServiceData.setaClass(resource.value());
            } else {
                invokeServiceData.setaClass(clazz.getName());
            }
            String requestId = UUID.randomUUID().toString();
            invokeServiceData.setRequestId(requestId);
            invokeServiceData.setServerName(resource.serverName());
            invokeServiceData.setObjects(args);
            return RpcClient.call(invokeServiceData);
        });
        return o1;
    }
}
