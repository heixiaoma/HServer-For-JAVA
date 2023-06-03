package cn.hserver.plugin.cloud;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 暴露出来的用户接口
 */
public interface DiscoveryHandler {

    //分组，服务名，list服务
    Map<String, Map<String, DynamicRoundRobin>> S_DATA = new ConcurrentHashMap<>();

    default void handler(String group, Map<String, List<ServerInstance>> data) {
        Map<String, DynamicRoundRobin> stringDynamicRoundRobinMap = S_DATA.get(group);
        if (stringDynamicRoundRobinMap == null) {
            stringDynamicRoundRobinMap = new ConcurrentHashMap<>();
            S_DATA.put(group, stringDynamicRoundRobinMap);
        }
        online(group, data);
        if (data.size() > 0) {
            data.forEach((k, v) -> {
                DynamicRoundRobin dynamicRoundRobin = S_DATA.get(group).get(k);
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
                    S_DATA.get(group).put(k, dynamicRoundRobin1);
                }
            });
        } else {
            S_DATA.get(group).forEach((k, v) -> v.removeAll());
            S_DATA.get(group).clear();
        }
    }

    /**
     * 服务上线回调
     * @param group
     * @param data
     */
    void online(String group, Map<String, List<ServerInstance>> data);

    /**
     * 获取一个有效的服务
     */
    default DynamicRoundRobin getDynamicRoundRobin(String group, String service) {
        final Map<String, DynamicRoundRobin> stringDynamicRoundRobinMap = S_DATA.get(group);
        if (stringDynamicRoundRobinMap == null) {
            return null;
        }
        return stringDynamicRoundRobinMap.get(service);
    }

}
