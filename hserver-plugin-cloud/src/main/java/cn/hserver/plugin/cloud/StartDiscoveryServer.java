package cn.hserver.plugin.cloud;

import cn.hserver.core.ioc.IocUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartDiscoveryServer {
    private static final Logger log = LoggerFactory.getLogger(StartDiscoveryServer.class);

    public static void init() {
        RegProp regProp = IocUtil.getBean(RegProp.class);
        if (regProp==null|| regProp.hasNull()){
            throw new RuntimeException("未添配置注册中心信息, 请检查 app.cloud.reg 下的配置信息");
        }
        final DiscoveryService bean = IocUtil.getBean(DiscoveryService.DISCOVERY_SERVICE, DiscoveryService.class);
        if (bean == null) {
            throw new RuntimeException("未添加注册中心依赖 如 nacos");
        }
        if (bean.register(regProp)){
            log.info("{}服务注册成功",regProp.getRegisterName());
        }else {
            throw new RuntimeException(String.format("%s 服务注册失败",regProp.getRegisterName()));
        }
    }
}
