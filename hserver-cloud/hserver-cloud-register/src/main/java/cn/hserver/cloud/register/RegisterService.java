package cn.hserver.cloud.register;


import cn.hserver.cloud.common.RegisterConfig;

/**
 * @author hxm
 */
public interface RegisterService {

    /**
     * 注册服务实例
     */
    boolean register(RegisterConfig registerConfig);

    /**
     * 注销服务
     */
    boolean deregister();

}