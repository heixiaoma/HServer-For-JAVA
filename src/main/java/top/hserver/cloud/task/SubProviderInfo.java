package top.hserver.cloud.task;

import com.alibaba.nacos.api.naming.listener.NamingEvent;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.CloudManager;
import top.hserver.cloud.client.NacosRpcClient;

import static top.hserver.cloud.CloudManager.naming;

/**
 * @author hxm
 */
@Slf4j
public class SubProviderInfo{

    public static void init() {
        //注册中心的
        try {
            for (String providerClass : CloudManager.getProviderClass()) {
               naming.subscribe(providerClass,event -> {
                   if (event instanceof NamingEvent){
                       NamingEvent evn = (NamingEvent) event;
                       NacosRpcClient.connect(evn);
                   }
               });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
