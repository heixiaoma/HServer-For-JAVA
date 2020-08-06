package top.hserver.cloud;


import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.bean.ClientData;
import top.hserver.cloud.config.AppRpc;
import top.hserver.cloud.config.AppRpcNacos;
import top.hserver.cloud.proxy.CloudProxy;
import top.hserver.cloud.task.*;
import top.hserver.core.ioc.IocUtil;
import top.hserver.core.task.TaskManager;

import javax.naming.event.NamingEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author hxm
 */
@Slf4j
public class CloudManager {
    /**
     * 服务提供的
     */
    private static Map<String, ClientData> serviceDataMap = new ConcurrentHashMap<>();

    /**
     * 消费者要消费的
     */
    private static Set<String> involve = new CopyOnWriteArraySet<>();
    /**
     * 所有的提供者的服务IP+port
     */
    private static Set<String> providersIpData = new CopyOnWriteArraySet<>();


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
            AppRpc appRpc = IocUtil.getBean(AppRpc.class);
            //1.读取自己是不是开启了rpc
            if (appRpc != null && appRpc.isOpen()) {
                String name = appRpc.getName();
                if (name == null) {
                    throw new NullPointerException("app.rpc.name不能为空");
                }
                if (appRpc.getMode() != null && "nacos".equalsIgnoreCase(appRpc.getMode())) {

                    AppRpcNacos appRpcNacos = IocUtil.getBean(AppRpcNacos.class);
                    if (appRpcNacos == null || appRpcNacos.getAddress() == null) {
                        throw new NullPointerException("Nacos注册中心不能为空");
                    }
                    if (appRpc.getIp() == null) {
                        throw new NullPointerException("Nacos模式，自己的IP不能为空");
                    }

                    /**
                     * nacos 客服端
                     */
                    naming = NamingFactory.createNamingService(appRpcNacos.getAddress());

                    if (appRpc.isType()) {
                        log.info("我是消费者");
                        TaskManager.addTask(ProviderInfo.class.getName(), "5000", ProviderInfo.class);
                        //维持长连接的任务
                        TaskManager.addTask(KeepLiveTask.class.getName(), "5000", KeepLiveTask.class);
                    } else {
                        log.info("我是提供者");
                        TaskManager.addTask(Broadcast1V1ProviderTask.class.getName(), "5000", Broadcast1V1ProviderTask.class, name);
                    }
                    /**
                     * 不论是消费者还生产者都要去注册中心注册
                     */
                    if (CloudManager.isRpcService()) {
                        for (String aClass : CloudManager.getClasses()) {
                            naming.registerInstance(aClass, appRpc.getIp(), port, name);
                        }
                    } else {
                        naming.registerInstance(name, appRpc.getIp(), port);
                    }
                } else {
                    String address = appRpc.getAddress();
                    if (address == null) {
                        throw new NullPointerException("app.rpc.address 不能为空");
                    }
                    if (appRpc.isType()) {
                        log.info("我是消费者");
                        /**
                         * 消费者连接提供者
                         * 服务提供者注册到消费中
                         */
                        TaskManager.addTask(Broadcast1V1ConsumerTask.class.getName(), "5000", Broadcast1V1ConsumerTask.class, address);
                    } else {
                        /**
                         *  当有消费者进来的时候，
                         *  发送自己的rpc信息给消费者
                         *
                         */
                        TaskManager.addTask(Broadcast1V1ProviderTask.class.getName(), "5000", Broadcast1V1ProviderTask.class, name);
                        log.info("我是提供者");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    /**
     * 服务提供者的
     *
     * @param name
     * @param classs
     */
    public static void add(String name, ClientData classs) {
        if (serviceDataMap.containsKey(name)) {
            log.warn("已经存在：{}Rpc服务", name);
            return;
        }
        serviceDataMap.put(name, classs);
    }

    /**
     * 消费者涉及到的
     *
     * @param name
     */
    public static void add(String name) {
        involve.add(name);
    }


    public static boolean isRpcService() {
        return serviceDataMap.size() > 0;
    }

    public static List<String> getClasses() {
        List<String> list = new ArrayList<>();
        serviceDataMap.forEach((a, b) -> list.add(b.getAClass()));
        return list;
    }

    public static Set<String> getProviderClass() {
        return involve;
    }

    public static ClientData get(String name) {
        return serviceDataMap.get(name);
    }

    public static void addAddress(String address) {
        providersIpData.add(address);
    }

    public static Set<String> getAddress() {
        return providersIpData;
    }

    public static void removeAddress(String address) {
        providersIpData.remove(address);
    }


}
