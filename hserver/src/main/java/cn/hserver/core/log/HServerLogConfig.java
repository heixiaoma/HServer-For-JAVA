package cn.hserver.core.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.server.context.ServerConfig;
import cn.hserver.core.server.util.ExceptionUtil;
import cn.hserver.core.server.util.PropUtil;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * @author hxm
 */
public class HServerLogConfig {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HServerLogConfig.class);
    public static void init() {
        try {
            ServerConfig serverConfig = IocUtil.getBean(ServerConfig.class);
            if (serverConfig.getLogbackName()!=null&&!serverConfig.getLogbackName().isEmpty()) {
                InputStream logbackName = HServerLogConfig.class.getResourceAsStream("/" + serverConfig.getLogbackName().trim());
                if (logbackName != null) {
                    loadConfiguration(logbackName);
                    return;
                } else {
                    System.err.println(serverConfig.getLogbackName().trim() + "文件未读取到，请将文件放置在 resources目录下");
                }
            }
            loadConfiguration(HServerLogConfig.class.getResourceAsStream("/logback-hserver.xml"));
        } catch (Exception e) {
            System.err.println(ExceptionUtil.getMessage(e));
        }
    }

    private static void stopAndReset(LoggerContext loggerContext) {
        loggerContext.stop();
        loggerContext.reset();
    }

    private static void loadConfiguration(InputStream in) throws Exception {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        stopAndReset(loggerContext);
        ch.qos.logback.classic.joran.JoranConfigurator configurator = new ch.qos.logback.classic.joran.JoranConfigurator();
        configurator.setContext(loggerContext);
        configurator.doConfigure(in);
        in.close();
        Logger root = loggerContext.getLogger("ROOT");
        if (root != null) {
            ServerConfig serverConfig = IocUtil.getBean(ServerConfig.class);
            if (serverConfig.getLog()!=null&&!serverConfig.getLog().isEmpty()) {
                root.setLevel(Level.toLevel(serverConfig.getLog().trim()));
            }
        }
    }
}
