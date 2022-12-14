package cn.hserver.plugin.cloud;

import cn.hserver.core.ioc.annotation.Bean;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Bean
public class DiscoveryHandler {

    private final static Map<String, DynamicRoundRobin> S_DATA = new ConcurrentHashMap<>();

    public void handler(Map<String, List<ServerInstance>> data) {
        if (data.size() > 0) {
            data.forEach((k, v) -> {
                DynamicRoundRobin dynamicRoundRobin = S_DATA.get(k);
                if (dynamicRoundRobin != null) {
                    dynamicRoundRobin.removeAll();
                    for (ServerInstance serverInstance : v) {
                        dynamicRoundRobin.add(serverInstance);
                    }
                } else {
                    DynamicRoundRobin dynamicRoundRobin1 = new DynamicRoundRobin();
                    for (ServerInstance serverInstance : v) {
                        dynamicRoundRobin1.add(serverInstance);
                    }
                    S_DATA.put(k, dynamicRoundRobin1);
                }
            });
        } else {
            S_DATA.forEach((k,v)-> v.removeAll());
            S_DATA.clear();
        }
    }


    /**
     * 获取一个
     */
    public DynamicRoundRobin getDynamicRoundRobin(String service) {
        return S_DATA.get(service);
    }

}
