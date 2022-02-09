package top.hserver.core.properties;

import io.netty.util.concurrent.DefaultEventExecutorGroup;
import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.util.NamedThreadFactory;
import top.hserver.core.server.util.PropUtil;

import java.util.*;

/**
 * @author hxm
 */

public class PropertiesInit {

    public static void configFile() {
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
        Integer humPort = instance.getInt("humPort");
        if (humPort != null) {
            ConstConfig.HUM_PORT = humPort;
        }
        String trackExtPackages = instance.get("trackExtPackages");
        if (trackExtPackages.trim().length() > 0) {
            ConstConfig.TRACK_EXT_PACKAGES=trackExtPackages.split(",");
        }

        String trackNoPackages = instance.get("trackNoPackages");
        if (trackNoPackages.trim().length() > 0) {
            ConstConfig.TRACK_NO_PACKAGES=trackNoPackages.split(",");
        }
        try {
            String portsStr = instance.get("ports");
            if (portsStr.trim().length() > 0) {
                String[] portStars = portsStr.split(",");
                Integer[] ports = new Integer[portStars.length];
                for (int i = 0; i < portStars.length; i++) {
                    ports[i] = Integer.parseInt(portStars[i]);
                }
                ConstConfig.PORTS = ports;
            }
        }catch (Throwable ignored){
        }
        if (instance.get("epoll").trim().length() > 0) {
            ConstConfig.EPOLL = Boolean.valueOf(instance.get("epoll"));
        }
        if (instance.get("appName").trim().length() > 0) {
            ConstConfig.APP_NAME = instance.get("appName");
        }
        if (instance.get("persistPath").trim().length() > 0) {
            ConstConfig.PERSIST_PATH = instance.get("persistPath");
        }
        if (instance.get("openHttp2").trim().length() > 0) {
            ConstConfig.openHttp2 = Boolean.valueOf(instance.get("openHttp2"));
        }
        if (instance.get("track").trim().length() > 0) {
            ConstConfig.TRACK = Boolean.valueOf(instance.get("track"));
        }
        if (instance.get("readLimit").trim().length() > 0) {
            ConstConfig.READ_LIMIT = Long.valueOf(instance.get("readLimit"));
        }
        if (instance.get("writeLimit").trim().length() > 0) {
            ConstConfig.WRITE_LIMIT = Long.valueOf(instance.get("writeLimit"));
        }
        if (instance.get("httpContentSize").trim().length() > 0) {
            ConstConfig.HTTP_CONTENT_SIZE = instance.getInt("httpContentSize");
        }
        Integer businessPool = instance.getInt("businessPool");
        if (businessPool != null && businessPool > 0) {
            ConstConfig.BUSINESS_EVENT = new DefaultEventExecutorGroup(businessPool, new NamedThreadFactory("hserver_business"));
        }
        if (businessPool != null && businessPool < 0) {
            ConstConfig.BUSINESS_EVENT = null;
        } else {
            ConstConfig.BUSINESS_EVENT = new DefaultEventExecutorGroup(50, new NamedThreadFactory("hserver_business"));
        }
    }
}
