package cn.hserver.core.aop;

import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;


/**
 * @author hxm
 */
public class HookProxyFactory {

    public static Object newProxyInstance(Class<?>[] clazz,Class<HookAdapter> handlerClass) throws InstantiationException, IllegalAccessException {
//
//        // 代理工厂
//        ProxyFactory proxyFactory = new ProxyFactory();
//        // 设置需要创建子类的父类
//        if (ProxyObject.class.isAssignableFrom(clazz)) {
//            return null;
//        }
//        proxyFactory.setSuperclass(clazz);
//        Object o = proxyFactory.createClass().newInstance();
//        ((ProxyObject) o).setHandler(new HserverMethodHandler(clazz, hookPageName));
//        return o;
//
        return null;
    }


}
