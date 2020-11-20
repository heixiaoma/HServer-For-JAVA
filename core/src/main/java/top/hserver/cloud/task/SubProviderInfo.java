package top.hserver.cloud.task;

import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.CloudManager;
import top.hserver.cloud.client.NacosRpcClient;

import java.util.HashMap;
import java.util.Map;

import static top.hserver.cloud.CloudManager.naming;

/**
 * @author hxm
 */
@Slf4j
public class SubProviderInfo {

    private final static Map<String, EventListener> MAP = new HashMap<>();

    public static void init() {
        //注册中心的
        CloudManager.getProviderClass().forEach(p -> {
            try {
                EventListener eventListener = MAP.get(p);
                if (eventListener != null) {
                    naming.unsubscribe(p, eventListener);
                    MAP.remove(p);
                }
                EventListener listener = event -> {
                    if (event instanceof NamingEvent) {
                        NamingEvent evn = (NamingEvent) event;
                        NacosRpcClient.connect(evn);
                    }
                };
                MAP.put(p, listener);
                naming.subscribe(p, listener);
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        });
    }
}
