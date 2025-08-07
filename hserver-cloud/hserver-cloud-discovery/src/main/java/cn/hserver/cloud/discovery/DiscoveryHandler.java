package cn.hserver.cloud.discovery;


import cn.hserver.cloud.common.ServerInstance;

import java.util.List;

/**
 * @author hxm
 */
public abstract class DiscoveryHandler {

    /**
     * 查询服务
     */
   public abstract List<ServerInstance> find(String group, String service);

    /**
     * 选择服务实例
     */
    public  ServerInstance choose(){
        return null;
    }

    /**
     * 订阅服务
     */
    public abstract void subscribe(String group, String service,DiscoveryListener discoveryListener);

}