package top.hserver.cloud;


import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.bean.ClientData;
import top.hserver.cloud.client.ChatClient;
import top.hserver.cloud.proxy.CloudProxy;
import top.hserver.cloud.server.RegServer;
import top.hserver.cloud.task.Broadcast1V1Task;
import top.hserver.cloud.task.BroadcastNacosTask;
import top.hserver.cloud.task.ConsumerInfoTask;
import top.hserver.cloud.util.NetUtil;
import top.hserver.core.server.util.PropUtil;
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

    private static final String TYPE1 = "1v1";

    private static final String TYPE2 = "nacos";

    private static Map<String, ClientData> serviceDataMap = new ConcurrentHashMap<>();

    public static void run() {
        //清除启动的Map缓存
        CloudProxy.clearCache();
        //1.读取自己是不是开启了云
        try {
            PropUtil propKit = PropUtil.getInstance();
            Boolean open = propKit.getBoolean("app.cloud.open");
            if (open != null && open) {
                port = Integer.parseInt(propKit.get("app.cloud.port", "9527"));
                String type = propKit.get("app.cloud.type");
                if (type.equalsIgnoreCase(TYPE1)) {
                    //2.自己是不是主机
                    Boolean masterOpen = propKit.getBoolean("app.cloud.master.open");
                    if (masterOpen != null && masterOpen) {
                        //开启监听从机动态
                        new RegServer(port).start();
                    }
                    //自己是不是从机
                    Boolean slaveOpen = propKit.getBoolean("app.cloud.slave.open");
                    if (slaveOpen != null && slaveOpen) {
                        //上报给主机自己的状态
                        String cloudName = propKit.get("app.cloud.slave.name");
                        if (cloudName == null || cloudName.trim().length() == 0) {
                            //获取内网IP
                            cloudName = NetUtil.getIpAddress();
                        } else {
                            cloudName = cloudName + "-->" + NetUtil.getIpAddress();
                        }
                        //启动聊天服务器
                        Object host;
                        try {
                            host = propKit.get("app.cloud.slave.master.host");
                            if (host != null) {
                                new ChatClient(host.toString(), CloudManager.port).start();
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage());
                            return;
                        }
                        TaskManager.addTask(Broadcast1V1Task.class.getName(), "5000", Broadcast1V1Task.class, cloudName, host);
                    }
                } else if (type.equalsIgnoreCase(TYPE2)) {

                    boolean flag;

                    String cloudName = propKit.get("app.cloud.slave.name", null);
                    String host = propKit.get("app.cloud.nacos.host", null);
                    String port = propKit.get("app.cloud.nacos.port", null);
                    String host1 = propKit.get("app.cloud.slave.host", null);
                    String port1 = propKit.get("app.cloud.slave.port", null);
                    if (host == null || port == null) {
                        log.error("nacos 地址有误");
                        return;
                    }
                    if (host1 == null || port1 == null || cloudName == null) {
                        flag = true;
                        host1 = propKit.get("app.cloud.master.host", null);
                        port1 = propKit.get("app.cloud.master.port", null);
                        cloudName = propKit.get("app.cloud.master.name", null);
                        if (host1 == null || port1 == null || cloudName == null) {
                            log.error("消费者或者生产者host或者port未填写");
                            return;
                        }
                        log.info("当前身份为：消费者");
                        //开启监听从机动态
                        new RegServer(CloudManager.port).start();
                    } else {
                        flag = false;
                        String serviceNames = propKit.get("app.cloud.serviceNames", null);
                        if (serviceNames == null) {
                            log.error("app.cloud.serviceNames不能为空");
                            return;
                        }
                        //动态的获取有效果的提供者
                        TaskManager.addTask(ConsumerInfoTask.class.getName(), "5000", ConsumerInfoTask.class, host, port, serviceNames);
                        log.info("当前身份为：提供者");
                    }
                    TaskManager.addTask(BroadcastNacosTask.class.getName(), "5000", BroadcastNacosTask.class, cloudName, host, port, host1, port1, flag);
                } else {
                    log.error("你开启了RPC模式，但是RPC类型有问题");
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
