package cn.hserver.cloud;


import java.util.List;

/**
 * @author hxm
 */
public interface DiscoveryService {

    String DISCOVERY_SERVICE = "DISCOVERY_SERVICE";


    /**
     * 注册服务实例
     */
    boolean register(RegProp regProp);

    /**
     * 注销服务
     */
    boolean deregister();

    /**
     * 查询服务
     */
    List<ServerInstance> find(String group, String service);


    /**
     * 订阅服务
     */
    void subscribe(String group, String service,DiscoveryHandler discoveryHandler);

}