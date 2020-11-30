package top.hserver.cloud;


import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hserver.cloud.client.RpcClient;
import top.hserver.cloud.client.handler.RpcClientHandler;
import top.hserver.cloud.config.AppRpc;
import top.hserver.cloud.config.AppRpcNacos;
import top.hserver.cloud.proxy.CloudProxy;
import top.hserver.cloud.task.*;
import top.hserver.core.ioc.IocUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author hxm
 */
public class CloudManager {
    private static final Logger log = LoggerFactory.getLogger(CloudManager.class);
    private static Set<String> ServerNames = new CopyOnWriteArraySet<>();
    /**
     * Nacos注册中心
     */
    public static NamingService naming;

    /**
     * 初始化rpc
     *
     * @param port
     */
    public static void run(Integer port) {
        //清除启动的Map缓存
        CloudProxy.clearCache();
        try {
            RpcClient.init();
            AppRpc appRpc = IocUtil.getBean(AppRpc.class);
            if (appRpc != null) {
                if (appRpc.getMode() != null && "nacos".equalsIgnoreCase(appRpc.getMode())) {
                    AppRpcNacos appRpcNacos = IocUtil.getBean(AppRpcNacos.class);
                    if (appRpcNacos == null || appRpcNacos.getAddress() == null) {
                        throw new NullPointerException("Nacos注册中心不能为空");
                    }
                    if (appRpcNacos.getName() == null) {
                        throw new NullPointerException("app.rpc.naocs.name不能为空");
                    }
                    if (appRpcNacos.getIp() == null) {
                        throw new NullPointerException("Nacos模式，自己的IP不能为空");
                    }
                    /**
                     * nacos 客服端
                     */
                    naming = NamingFactory.createNamingService(appRpcNacos.getAddress());
                    /**
                     * 不论是消费者还生产者都要去注册中心注册
                     */
                    if (appRpcNacos.getGroup() == null || appRpcNacos.getGroup().trim().length() == 0) {
                        appRpcNacos.setGroup(Constants.DEFAULT_GROUP);
                    }
                    naming.registerInstance(appRpcNacos.getName(), appRpcNacos.getGroup(), appRpcNacos.getIp(), port, appRpcNacos.getName());
                    /**
                     * 订阅注册的数据
                     */
                    SubProviderInfo.init();
                } else {
                    String address = appRpc.getAddress();
                    if (address != null) {
                        RpcClientHandler.defaultReg(address);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }


    /**
     * 消费者涉及，通过服务名去订阅Nacos
     *
     * @param name
     */
    public static void add(String name) {
        ServerNames.add(name);
    }

    public static List<String> getServerNames() {
        return new ArrayList<>(ServerNames);
    }
}
