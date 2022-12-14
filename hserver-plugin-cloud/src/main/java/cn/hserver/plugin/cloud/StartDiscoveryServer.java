package cn.hserver.plugin.cloud;

import cn.hserver.core.interfaces.InitRunner;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.annotation.Autowired;
import cn.hserver.core.ioc.annotation.Bean;

@Bean
public class StartDiscoveryServer implements InitRunner {

    @Autowired
    private RegProp regProp;

    @Override
    public void init(String[] args) {
        //对服务进行注册
        final DiscoveryService bean = IocUtil.getBean(DiscoveryService.DISCOVERY_SERVICE, DiscoveryService.class);
        bean.register(regProp);
    }
}
