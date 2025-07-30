package cn.hserver.core.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import cn.hserver.core.config.ServerConfig;
import cn.hserver.core.context.AnnotationConfigApplicationContext;
import cn.hserver.core.util.JarInputStreamUtil;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * @author hxm
 */
public class HServerLogConfig {
    public static void init() {
        try {
            ServerConfig serverConfig = AnnotationConfigApplicationContext.getBean(ServerConfig.class);
            if (serverConfig!=null&&serverConfig.getLogbackName() != null && !serverConfig.getLogbackName().isEmpty()) {
                InputStream logbackName = HServerLogConfig.class.getResourceAsStream("/" + serverConfig.getLogbackName().trim());
                if (logbackName != null) {
                    loadConfiguration(logbackName,serverConfig);
                    return;
                } else {
                    System.err.println(serverConfig.getLogbackName().trim() + "文件未读取到，请将文件放置在 resources目录下");
                }
            }
            loadConfiguration(HServerLogConfig.class.getResourceAsStream("/logback-hserver.xml"),null);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static void stopAndReset(LoggerContext loggerContext) {
        loggerContext.stop();
        loggerContext.reset();
    }

    private static void loadConfiguration(InputStream in,ServerConfig serverConfig) throws Exception {
        in = JarInputStreamUtil.decrypt(in);
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        stopAndReset(loggerContext);
        ch.qos.logback.classic.joran.JoranConfigurator configurator = new ch.qos.logback.classic.joran.JoranConfigurator();
        configurator.setContext(loggerContext);
        configurator.doConfigure(in);
        in.close();
        Logger root = loggerContext.getLogger("ROOT");
        if (root != null) {
            if (serverConfig!=null&&serverConfig.getLog() != null && !serverConfig.getLog().isEmpty()) {
                root.setLevel(Level.toLevel(serverConfig.getLog().trim()));
            }
        }
    }
}
