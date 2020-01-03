package top.hserver.cloud;


import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.bean.ClientData;
import top.hserver.cloud.client.RegClient;
import top.hserver.cloud.server.RegServer;
import top.hserver.cloud.task.BroadcastTask;
import top.hserver.cloud.util.NetUtil;
import top.hserver.core.task.TaskManager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CloudManager {

    public final static int port=9527;

    private static Map<String, ClientData> serviceDataMap = new ConcurrentHashMap<>();

    public static void run() {
        //1.读取自己是不是开启了云
        try {
            Properties pps = new Properties();
            InputStream resourceAsStream = CloudManager.class.getResourceAsStream("/application.properties");
            pps.load(resourceAsStream);
            Object open = pps.get("app.cloud.open");
            if (open != null && Boolean.valueOf(open.toString())) {
                //2.自己是不是主机
                Object master_open = pps.get("app.cloud.master.open");
                if (master_open != null && Boolean.valueOf(master_open.toString())) {
                    //开启监听从机动态
                    new RegServer().start();
                }

                //自己是不是从机
                Object slave_open = pps.get("app.cloud.slave.open");
                if (slave_open != null && Boolean.valueOf(slave_open.toString())) {
                    //上报给主机自己的状态
                    Object cloud_name = pps.get("app.cloud.name");
                    if (cloud_name == null) {
                        //获取内网IP
                        cloud_name = NetUtil.getIpAddress();
                    } else {
                        cloud_name = cloud_name + "-->" + NetUtil.getIpAddress();
                    }
                    new RegClient().start();
                    TaskManager.addTask(cloud_name.toString(), 5000, BroadcastTask.class, cloud_name.toString());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public static void add(String name, ClientData classs) {

        if (serviceDataMap.containsKey(name)) {
            log.warn("已经存在：" + name + "Rpc服务");
            return;
        }
        serviceDataMap.put(name, classs);
    }

    public static boolean isRpcService() {
        return serviceDataMap.size() > 0 ? true : false;
    }

    public static List<String> getClasses() {
        List<String> list=new ArrayList<>();
        serviceDataMap.forEach((a,b)->list.add(b.getAClass()));
        return list;
    }

    public static ClientData get(String name){
       return serviceDataMap.get(name);
    }
}
