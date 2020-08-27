package top.hserver.core.properties;

import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.util.NamedThreadFactory;
import top.hserver.core.server.util.PropUtil;

import java.util.*;

import static top.hserver.core.properties.NacosProperties.nacosConfig;

/**
 * @author hxm
 */

@Slf4j
public class PropertiesInit {

    public static void configFile(Set<String> scanPackage) {
        PropUtil instance = PropUtil.getInstance();
        Integer taskPool = instance.getInt("taskPool");
        if (taskPool != null) {
            ConstConfig.taskPool = taskPool;
        }
        Integer queuePool = instance.getInt("queuePool");
        if (taskPool != null) {
            ConstConfig.queuePool = queuePool;
        }
        Integer bossPool = instance.getInt("bossPool");
        if (bossPool != null) {
            ConstConfig.bossPool = bossPool;
        }
        Integer workerPool = instance.getInt("workerPool");
        if (workerPool != null) {
            ConstConfig.workerPool = workerPool;
        }
        ConstConfig.EPOLL = Boolean.valueOf(instance.get("epoll"));
        Integer businessPool = instance.getInt("businessPool");
        if (businessPool != null) {
            ConstConfig.BUSINESS_EVENT = new DefaultEventExecutorGroup(businessPool, new NamedThreadFactory("hserver_business"));
        }

        String config = instance.get("app.nacos.config.address", null);

        if (config != null) {
            log.info("初始化配置中心");
            nacosConfig(config, scanPackage);
            log.info("初始化配置中心完成");
        }
    }
}
