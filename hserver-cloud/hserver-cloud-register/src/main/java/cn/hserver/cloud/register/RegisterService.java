package cn.hserver.cloud.register;


import cn.hserver.cloud.common.RegProp;

/**
 * @author hxm
 */
public interface RegisterService {

    /**
     * 注册服务实例
     */
    boolean register(RegProp regProp);

    /**
     * 注销服务
     */
    boolean deregister();

}