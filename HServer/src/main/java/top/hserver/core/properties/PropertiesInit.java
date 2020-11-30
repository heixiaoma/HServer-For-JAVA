package top.hserver.core.properties;

import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.util.NamedThreadFactory;
import top.hserver.core.server.util.PropUtil;

import java.util.*;

import static top.hserver.core.properties.NacosProperties.nacosConfig;

/**
 * @author hxm
 */

public class PropertiesInit {

    private static final Logger log = LoggerFactory.getLogger(PropertiesInit.class);

    public static void configFile(Set<String> scanPackage) {
        PropUtil instance = PropUtil.getInstance();
        Integer taskPool = instance.getInt("taskPool");
        if (taskPool != null) {
            ConstConfig.taskPool = taskPool;
        }
        Integer bossPool = instance.getInt("bossPool");
        if (bossPool != null) {
            ConstConfig.bossPool = bossPool;
        }
        Integer workerPool = instance.getInt("workerPool");
        if (workerPool != null) {
            ConstConfig.workerPool = workerPool;
        }
        if (instance.get("epoll").trim().length() > 0) {
            ConstConfig.EPOLL = Boolean.valueOf(instance.get("epoll"));
        }
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
