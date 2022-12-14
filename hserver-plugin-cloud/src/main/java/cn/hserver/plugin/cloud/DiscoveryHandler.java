package cn.hserver.plugin.cloud;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DiscoveryHandler {

    private final static Map<String, DynamicRoundRobin> S_DATA = new ConcurrentHashMap<>();

    public void handler(ServerInstance serverInstance) {
        final String serviceName = serverInstance.getServiceName();
        final DynamicRoundRobin dynamicRoundRobin = S_DATA.get(serviceName);
        if (dynamicRoundRobin == null) {
            S_DATA.put(serviceName, new DynamicRoundRobin(serverInstance));
        } else {
            dynamicRoundRobin.add(serverInstance);
        }
    }
}
