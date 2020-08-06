package top.hserver.cloud;


import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.bean.ClientData;
import top.hserver.cloud.config.AppRpc;
import top.hserver.cloud.proxy.CloudProxy;
import top.hserver.cloud.task.Broadcast1V1ConsumerTask;
import top.hserver.cloud.task.Broadcast1V1ProviderTask;
import top.hserver.core.ioc.IocUtil;
import top.hserver.core.task.TaskManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hxm
 */
@Slf4j
public class CloudManager {

    public static int port = 9527;

    private static Map<String, ClientData> serviceDataMap = new ConcurrentHashMap<>();

    public static void run(Integer port) {
        CloudManager.port = port;
        //清除启动的Map缓存
        CloudProxy.clearCache();
        try {
            AppRpc appRpc = IocUtil.getBean(AppRpc.class);
            //1.读取自己是不是开启了rpc
            if (appRpc!=null&&appRpc.isOpen()) {
                if (appRpc.getType().equalsIgnoreCase("nacos")) {
//                    boolean flag;
//                    String cloudName = propKit.get("app.cloud.slave.name", null);
//                    String host = propKit.get("app.cloud.nacos.host", null);
//                    String port = propKit.get("app.cloud.nacos.port", null);
//                    String host1 = propKit.get("app.cloud.slave.host", null);
//                    String port1 = propKit.get("app.cloud.slave.port", null);
//                    if (host == null || port == null) {
//                        log.error("nacos 地址有误");
//                        return;
//                    }
//                    if (host1 == null || port1 == null || cloudName == null) {
//                        flag = true;
//                        host1 = propKit.get("app.cloud.master.host", null);
//                        port1 = propKit.get("app.cloud.master.port", null);
//                        cloudName = propKit.get("app.cloud.master.name", null);
//                        if (host1 == null || port1 == null || cloudName == null) {
//                            log.error("消费者或者生产者host或者port未填写");
//                            return;
//                        }
//                        log.info("当前身份为：消费者");
//                        //开启监听从机动态
//                    } else {
//                        flag = false;
//                        String serviceNames = propKit.get("app.cloud.serviceNames", null);
//                        if (serviceNames == null) {
//                            log.error("app.cloud.serviceNames不能为空");
//                            return;
//                        }
//                        //动态的获取有效果的提供者
//                        TaskManager.addTask(ConsumerInfoTask.class.getName(), "5000", ConsumerInfoTask.class, host, port, serviceNames);
//                        log.info("当前身份为：提供者");
//                    }
//                    TaskManager.addTask(BroadcastNacosTask.class.getName(), "5000", BroadcastNacosTask.class, cloudName, host, port, host1, port1, flag);

                } else {
                    String address = appRpc.getAddress();
                    if (address != null&&address.trim().length()>0) {
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
                        TaskManager.addTask(Broadcast1V1ProviderTask.class.getName(), "5000", Broadcast1V1ProviderTask.class, "Server1");
                        log.info("我是提供者");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    public static void add(String name, ClientData classs) {

        if (serviceDataMap.containsKey(name)) {
            log.warn("已经存在：{}Rpc服务", name);
            return;
        }
        serviceDataMap.put(name, classs);
    }

    public static boolean isRpcService() {
        return serviceDataMap.size() > 0;
    }

    public static List<String> getClasses() {
        List<String> list = new ArrayList<>();
        serviceDataMap.forEach((a, b) -> list.add(b.getAClass()));
        return list;
    }

    public static ClientData get(String name) {
        return serviceDataMap.get(name);
    }
}
