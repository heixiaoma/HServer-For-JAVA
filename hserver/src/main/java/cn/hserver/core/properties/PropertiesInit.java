package cn.hserver.core.properties;

import cn.hserver.core.server.context.ConstConfig;
import cn.hserver.core.server.util.PropUtil;
import cn.hserver.core.server.util.EventLoopUtil;

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
        Integer workerPool = instance.getInt("workerPool");
        if (workerPool != null) {
            ConstConfig.workerPool = workerPool;
        }

        Integer backLog = instance.getInt("backLog");
        if (backLog != null) {
            ConstConfig.backLog = backLog;
        }

        String humOpen = instance.get("humOpen");
        if (humOpen != null) {
            ConstConfig.HUM_OPEN = Boolean.valueOf(humOpen);
        }
        Integer humPort = instance.getInt("humPort");
        if (humPort != null) {
            ConstConfig.HUM_PORT = humPort;
        }

        Integer PRE_PROTOCOL_MAX_SIZE = instance.getInt("preProtocolMaxSize");
        if (PRE_PROTOCOL_MAX_SIZE != null) {
            ConstConfig.PRE_PROTOCOL_MAX_SIZE = PRE_PROTOCOL_MAX_SIZE;
        }

        String trackExtPackages = instance.get("trackExtPackages");
        if (!trackExtPackages.trim().isEmpty()) {
            ConstConfig.TRACK_EXT_PACKAGES=trackExtPackages.split(",");
        }

        String trackNoPackages = instance.get("trackNoPackages");
        if (!trackNoPackages.trim().isEmpty()) {
            ConstConfig.TRACK_NO_PACKAGES=trackNoPackages.split(",");
        }
        try {
            String portsStr = instance.get("ports");
            if (!portsStr.trim().isEmpty()) {
                String[] portStars = portsStr.split(",");
                Integer[] ports = new Integer[portStars.length];
                for (int i = 0; i < portStars.length; i++) {
                    ports[i] = Integer.parseInt(portStars[i]);
                }
                ConstConfig.PORTS = ports;
            }
        }catch (Throwable ignored){
        }
        if (!instance.get("appName").trim().isEmpty()) {
            ConstConfig.APP_NAME = instance.get("appName");
        }
        if (!instance.get("persistPath").trim().isEmpty()) {
            ConstConfig.PERSIST_PATH = instance.get("persistPath");
        }
        if (!instance.get("track").trim().isEmpty()) {
            ConstConfig.TRACK = Boolean.valueOf(instance.get("track"));
        }
    }
}
