package cn.hserver.cloud.discovery;


import cn.hserver.cloud.common.ServerInstance;

import java.util.List;

/**
 * @author hxm
 */
public abstract class DiscoveryService {

    /**
     * 查询服务
     */
   public abstract List<ServerInstance> find(String group, String service);

    /**
     * 更具权重查询一个服务
     * @param group
     * @param service
     * @return
     */
   public abstract ServerInstance findOne(String group, String service);
    /**
     * 订阅服务
     */
    public abstract void subscribe(String group, String service,DiscoveryListener discoveryListener);

}