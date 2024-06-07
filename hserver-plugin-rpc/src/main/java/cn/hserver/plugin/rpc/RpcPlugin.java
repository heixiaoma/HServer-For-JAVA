package cn.hserver.plugin.rpc;


import cn.hserver.core.ioc.ref.PackageScanner;
import cn.hserver.plugin.cloud.DiscoveryService;
import cn.hserver.plugin.rpc.annotation.Resource;
import cn.hserver.plugin.rpc.annotation.RpcService;
import cn.hserver.plugin.rpc.bean.ServerInfo;
import cn.hserver.plugin.rpc.core.ClientProxy;
import cn.hserver.plugin.rpc.core.RpcDisHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.hserver.core.interfaces.PluginAdapter;
import cn.hserver.core.ioc.IocUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author hxm
 */
public class RpcPlugin implements PluginAdapter {

    private static final Logger log = LoggerFactory.getLogger(RpcPlugin.class);
    private static final Set<ServerInfo> ServerNames = new CopyOnWriteArraySet<>();

    @Override
    public void startApp() {

    }

    @Override
    public void startIocInit() {

    }

    @Override
    public Set<Class<?>> iocInitBeanList() {
        return null;
    }

    @Override
    public void iocInit(PackageScanner packageScanner) {

    }

    @Override
    public void iocInitEnd() {

    }

    @Override
    public void startInjection() {

    }

    /**
     * 注入完成
     */
    @Override
    public void injectionEnd() {
        //rpc注入
        rpc();
        //服务注册
        serverReg();
    }

    //订阅服务
    private void serverReg() {
        DiscoveryService DISCOVERY_SERVICE = IocUtil.getBean(DiscoveryService.DISCOVERY_SERVICE, DiscoveryService.class);
        for (ServerInfo serverInfo : ServerNames) {
            DISCOVERY_SERVICE.subscribe(serverInfo.getGroupName(), serverInfo.getServerName(), RpcDisHandler.getRpcDisHandler());
        }
    }
    private void rpc() {
        //rpc注入开始
        Map<String, Object> all = IocUtil.getAll();
        all.forEach((k, v) -> {
            //注意有一个List类型的IOC
            if (v instanceof List) {
                List v1 = (List) v;
                for (Object o : v1) {
                    autoZr(o);
                }
            } else {
                autoZr(v);
                //rpc调用的接口默认注入的子类
                changeRpcService(v);
            }

        });
    }


    public void changeRpcService(Object o) {
        //检测当前的Bean是不是Rpc服务
        RpcService rpcService = o.getClass().getAnnotation(RpcService.class);
        //说明是rpc服务，单独存储一份她的数据哦
        if (rpcService != null) {
            if (rpcService.value().trim().length() > 0) {
                //自定义了Rpc服务名
                IocUtil.addBean(rpcService.value(), o);
            } else {
                //没有自定义服务名字
                Class<?>[] interfaces = o.getClass().getInterfaces();
                if (interfaces.length > 0) {
                    IocUtil.addBean(interfaces[0].getName(), o);
                } else {
                    log.error("RPC没有实现任何接口，预计调用过程会出现问题:{}", o.getClass().getSimpleName());
                }
            }
        }
    }

    private void autoZr(Object v) {
        Class<?> par = v.getClass();
        while (!par.equals(Object.class)) {
            //获取当前类的所有字段
            Field[] declaredFields = par.getDeclaredFields();
            for (Field field : declaredFields) {
                //rpc注入
                rpczr(field, v);
            }
            par = par.getSuperclass();
        }
    }

    /**
     * Rpc 服务的代理对象生成
     */
    private void rpczr(Field declaredField, Object v) {
        Resource annotation = declaredField.getAnnotation(Resource.class);
        if (annotation != null) {
            try {
                check(declaredField.getType());
                ServerNames.add(new ServerInfo(annotation.serverName(), annotation.groupName()));
                declaredField.setAccessible(true);
                Object proxy = ClientProxy.getProxy(declaredField.getType(), annotation);
                declaredField.set(v, proxy);
                log.info("{}----->{}：装配完成，Rpc装配", proxy.getClass().getSimpleName(), v.getClass().getSimpleName());
            } catch (Exception e) {
                log.error("{}----->{}：装配错误:RPC代理生成失败", v.getClass().getSimpleName(), v.getClass().getSimpleName());
                throw new RuntimeException(e);
            }
        }
    }

    private void check(Class<?> aClass) {
        Method[] declaredMethods = aClass.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (!declaredMethod.getReturnType().isAssignableFrom(CompletableFuture.class)) {
                log.warn("类：{}，方法：{},返回值不是CompletableFuture，RPC异步调用过程中会出现问题", aClass.getName(), declaredMethod.getName());
            }
        }
    }

}
