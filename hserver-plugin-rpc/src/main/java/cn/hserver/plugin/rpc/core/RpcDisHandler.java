package cn.hserver.plugin.rpc.core;

import cn.hserver.plugin.cloud.DiscoveryHandler;
import cn.hserver.plugin.cloud.DynamicRoundRobin;
import cn.hserver.plugin.cloud.ServerInstance;
import cn.hserver.plugin.rpc.codec.ServiceData;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcDisHandler implements DiscoveryHandler {

    private final static RpcDisHandler RPC_DIS_HANDLER = new RpcDisHandler();

    private final static Map<String, ServiceData> SERVICE_DATA_MAP = new ConcurrentHashMap<>();

    public static RpcDisHandler getRpcDisHandler() {
        return RPC_DIS_HANDLER;
    }

    /**
     * 触发，我就清除下
     */
    @Override
    public void online(String group,Map<String, List<ServerInstance>> data) {
        SERVICE_DATA_MAP.forEach((k, v) -> {
            v.closeChannelPool();
        });
        SERVICE_DATA_MAP.clear();
    }

    public ServiceData chose(String groupName,String service) {
        //内部的
        final DynamicRoundRobin dynamicRoundRobin = getDynamicRoundRobin(groupName,service);
        final ServerInstance choose = dynamicRoundRobin.choose();
        if (choose == null) {
            return null;
        }

        //缓存有就走缓存
        ServiceData serviceData = SERVICE_DATA_MAP.get(choose.getEq());
        if (serviceData != null) {
            return serviceData;
        }
        //缓存没有就初始一个
        serviceData = new ServiceData();
        serviceData.setIp(choose.getIp());
        serviceData.setPort(choose.getPort());
        serviceData.setServerName(choose.getClusterName());
        serviceData.initChannelPool();
        SERVICE_DATA_MAP.put(choose.getEq(), serviceData);
        return serviceData;
    }


}
