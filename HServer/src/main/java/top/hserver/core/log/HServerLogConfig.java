package top.hserver.core.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;
import top.hserver.core.server.util.ExceptionUtil;
import top.hserver.core.server.util.PropUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author hxm
 */
public class HServerLogConfig {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(HServerLogConfig.class);

    private static String[] getStandardConfigLocations() {
        return new String[]{"logback-test.groovy", "logback-test.xml", "logback.groovy", "logback.xml"};
    }

    public static void init() {
        try {
            if (existConfig()) {
                return;
            }
            if (PropUtil.getInstance().get("logbackName").trim().length() > 0) {
                InputStream logbackName = HServerLogConfig.class.getResourceAsStream("/" + PropUtil.getInstance().get("logbackName").trim());
                if (logbackName != null) {
                    loadConfiguration(logbackName);
                    return;
                } else {
                    System.err.println(PropUtil.getInstance().get("logbackName").trim() + "文件未读取到，请将文件放置在 resources目录下");
                }
            }
            loadConfiguration(HServerLogConfig.class.getResourceAsStream("/logback-hserver.xml"));
        } catch (Exception e) {
            log.error(ExceptionUtil.getMessage(e));
        }
    }

    private static boolean existConfig() {
        for (String s : getStandardConfigLocations()) {
            InputStream resourceAsStream = HServerLogConfig.class.getResourceAsStream("/" + s);
            if (resourceAsStream != null) {
                try {
                    resourceAsStream.close();
                } catch (IOException ignored) {
                }
                return true;
            }
        }
        return false;
    }

    private static void stopAndReset(LoggerContext loggerContext) {
        loggerContext.stop();
        loggerContext.reset();
    }

    private static void loadConfiguration(InputStream in) throws Exception {
        LoggerContext loggerContext = (LoggerContext) StaticLoggerBinder.getSingleton().getLoggerFactory();
        stopAndReset(loggerContext);
        ch.qos.logback.classic.joran.JoranConfigurator configurator = new ch.qos.logback.classic.joran.JoranConfigurator();
        configurator.setContext(loggerContext);
        configurator.doConfigure(in);
        in.close();
        Logger root = loggerContext.getLogger("ROOT");
        if (root != null) {
            if (PropUtil.getInstance().get("level").trim().length() > 0) {
                root.setLevel(Level.toLevel(PropUtil.getInstance().get("level").trim()));
            }
        }
    }
}
