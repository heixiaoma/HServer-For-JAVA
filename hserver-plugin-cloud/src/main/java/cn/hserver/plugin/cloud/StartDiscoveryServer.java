package cn.hserver.plugin.cloud;

import cn.hserver.core.ioc.IocUtil;

public class StartDiscoveryServer{

    public static void init() {
        RegProp regProp = IocUtil.getBean(RegProp.class);
        final DiscoveryService bean = IocUtil.getBean(DiscoveryService.DISCOVERY_SERVICE, DiscoveryService.class);
        bean.register(regProp);
    }
}
