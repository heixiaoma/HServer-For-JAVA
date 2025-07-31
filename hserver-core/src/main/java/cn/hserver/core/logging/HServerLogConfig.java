package cn.hserver.core.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import cn.hserver.core.config.ConstConfig;
import cn.hserver.core.util.JarInputStreamUtil;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * @author hxm
 */
public class HServerLogConfig {
    public static void init() {
        try {
            loadConfiguration(HServerLogConfig.class.getResourceAsStream("/"+ ConstConfig.LOGBACK_NAME));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static void stopAndReset(LoggerContext loggerContext) {
        loggerContext.stop();
        loggerContext.reset();
    }

    private static void loadConfiguration(InputStream in) throws Exception {
        in = JarInputStreamUtil.decrypt(in);
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        stopAndReset(loggerContext);
        ch.qos.logback.classic.joran.JoranConfigurator configurator = new ch.qos.logback.classic.joran.JoranConfigurator();
        configurator.setContext(loggerContext);
        configurator.doConfigure(in);
        in.close();
        Logger root = loggerContext.getLogger("ROOT");
        if (root != null) {
            root.setLevel(Level.toLevel(ConstConfig.LOG_LEVEL));
        }
    }
}
