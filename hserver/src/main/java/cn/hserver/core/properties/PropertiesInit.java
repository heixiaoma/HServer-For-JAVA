package cn.hserver.core.properties;

import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.server.context.ConstConfig;
import cn.hserver.core.server.context.IoMultiplexer;
import cn.hserver.core.server.context.ServerConfig;
import cn.hserver.core.server.util.ObjConvertUtil;
import cn.hserver.core.server.util.PropUtil;
import cn.hserver.core.server.util.EventLoopUtil;

import java.lang.reflect.Field;

/**
 * @author hxm
 */

public class PropertiesInit {

    public static void configFile() {
        ServerConfig serverConfig = new ServerConfig();
        try {
            for (Field field : ServerConfig.class.getDeclaredFields()) {
                PropUtil instance = PropUtil.getInstance();
                String s = instance.get(field.getName(), null);
                Object convert = ObjConvertUtil.convert(field.getType(), s);
                if (convert != null) {
                    field.setAccessible(true);
                    field.set(serverConfig, convert);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        IocUtil.addBean(serverConfig);
        if (serverConfig.getTaskPool() != null) {
            ConstConfig.taskPool = serverConfig.getTaskPool();
        }
        if (serverConfig.getWorkerPool() != null) {
            ConstConfig.workerPool = serverConfig.getWorkerPool();
        }

        if (serverConfig.getBackLog() != null) {
            ConstConfig.backLog = serverConfig.getBackLog();
        }

        if (serverConfig.getHumOpen() != null) {
            ConstConfig.HUM_OPEN = serverConfig.getHumOpen();
        }
        if (serverConfig.getHumPort() != null) {
            ConstConfig.HUM_PORT = serverConfig.getHumPort();
        }

        if (serverConfig.getPreProtocolMaxSize() != null) {
            ConstConfig.PRE_PROTOCOL_MAX_SIZE = serverConfig.getPreProtocolMaxSize();
        }

        if (serverConfig.getTrackExtPackages() != null && !serverConfig.getTrackExtPackages().trim().isEmpty()) {
            ConstConfig.TRACK_EXT_PACKAGES = serverConfig.getTrackExtPackages().split(",");
        }

        if (serverConfig.getTrackNoPackages() != null && !serverConfig.getTrackNoPackages().trim().isEmpty()) {
            ConstConfig.TRACK_NO_PACKAGES = serverConfig.getTrackNoPackages().split(",");
        }
        try {
            String portsStr = serverConfig.getPorts();
            if (portsStr != null && !portsStr.trim().isEmpty()) {
                String[] portStars = portsStr.split(",");
                Integer[] ports = new Integer[portStars.length];
                for (int i = 0; i < portStars.length; i++) {
                    ports[i] = Integer.parseInt(portStars[i]);
                }
                ConstConfig.PORTS = ports;
            }
        } catch (Throwable ignored) {
        }

        if (serverConfig.getAppName() != null && !serverConfig.getAppName().trim().isEmpty()) {
            ConstConfig.APP_NAME = serverConfig.getAppName();
        }
        if (serverConfig.getPersistPath() != null && !serverConfig.getPersistPath().trim().isEmpty()) {
            ConstConfig.PERSIST_PATH = serverConfig.getPersistPath();
        }
        if (serverConfig.getTrack() != null) {
            ConstConfig.TRACK = serverConfig.getTrack();
        }
        if (serverConfig.getIoMode()!=null){
            ConstConfig.IO_MOD = IoMultiplexer.valueOf(serverConfig.getIoMode());
        }

    }
}
