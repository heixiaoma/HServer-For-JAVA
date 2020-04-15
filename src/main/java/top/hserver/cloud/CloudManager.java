package top.hserver.cloud;


import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.bean.ClientData;
import top.hserver.cloud.client.ChatClient;
import top.hserver.cloud.proxy.CloudProxy;
import top.hserver.cloud.server.RegServer;
import top.hserver.cloud.task.BroadcastTask;
import top.hserver.cloud.util.NetUtil;
import top.hserver.core.server.util.PropUtil;
import top.hserver.core.task.TaskManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CloudManager {

  public static int port = 9527;

  private static Map<String, ClientData> serviceDataMap = new ConcurrentHashMap<>();

  public static void run() {
    //清除启动的Map缓存
    CloudProxy.clearCache();
    //1.读取自己是不是开启了云
    try {
      PropUtil propKit = new PropUtil();
      Object open = propKit.get("app.cloud.open");
      if (open != null && Boolean.parseBoolean(open.toString())) {
        //2.自己是不是主机
        Object master_open = propKit.get("app.cloud.master.open");
        if (master_open != null && Boolean.parseBoolean(master_open.toString())) {
          //开启监听从机动态
          new RegServer(port).start();
        }
        port=Integer.parseInt(propKit.get("app.cloud.port","9527"));
        //自己是不是从机
        Object slave_open = propKit.get("app.cloud.slave.open");
        if (slave_open != null && Boolean.parseBoolean(slave_open.toString())) {
          //上报给主机自己的状态
          Object cloud_name = propKit.get("app.cloud.slave.name");
          if (cloud_name == null) {
            //获取内网IP
            cloud_name = NetUtil.getIpAddress();
          } else {
            cloud_name = cloud_name + "-->" + NetUtil.getIpAddress();
          }
          //启动聊天服务器
          Object host = null;
          try {
            host = propKit.get("app.cloud.slave.master.host");
            if (host != null) {
              new ChatClient(host.toString(), CloudManager.port).start();
            }
          } catch (Exception e) {
            log.error(e.getMessage());
          }
          TaskManager.addTask(cloud_name.toString(), "5000", BroadcastTask.class, cloud_name.toString(), host);
        }
      }
    } catch (Exception e) {
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
