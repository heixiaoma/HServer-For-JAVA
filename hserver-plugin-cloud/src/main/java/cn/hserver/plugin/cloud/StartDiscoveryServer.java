package cn.hserver.plugin.cloud;

import cn.hserver.core.interfaces.InitRunner;
import cn.hserver.core.ioc.IocUtil;

public class StartDiscoveryServer implements InitRunner {
    @Override
    public void init(String[] args) {
        final DiscoveryService bean = IocUtil.getBean(DiscoveryService.DISCOVERY_SERVICE, DiscoveryService.class);
    }
}
