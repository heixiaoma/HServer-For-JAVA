package top.hserver.core.proxy;

import javassist.util.proxy.ProxyObject;
import top.hserver.core.interfaces.HookAdapter;
import top.hserver.core.ioc.IocUtil;
import javassist.util.proxy.ProxyFactory;

/**
 * @author hxm
 */
public class JavassistProxyFactory {

    public Object newProxyInstance(Class clazz, String hookPageName, String method) throws InstantiationException, IllegalAccessException {
        // 代理工厂
        ProxyFactory proxyFactory = new ProxyFactory();
        // 设置需要创建子类的父类
        proxyFactory.setSuperclass(clazz);
        Object o = proxyFactory.createClass().newInstance();
        ((ProxyObject)o).setHandler((self, thismethod, proceed, args) -> {
            HookAdapter hookAdapter = (HookAdapter) IocUtil.getBean(hookPageName);
            if (thismethod.getName().equals(method)) {
                hookAdapter.before(args);
            }
            Object result = proceed.invoke(self, args);
            if (thismethod.getName().equals(method)) {
                result = hookAdapter.after(result);
            }
            return result;
        });
        return o;
    }

}
