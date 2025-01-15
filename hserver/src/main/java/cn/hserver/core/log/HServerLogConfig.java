package cn.hserver.core.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.server.context.ServerConfig;
import cn.hserver.core.server.util.ExceptionUtil;
import cn.hserver.core.server.util.PropUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * @author hxm
 */
public class HServerLogConfig {
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

    private static void loadConfiguration(InputStream in) throws Exception {
        // 配置日志输出格式（包含高亮）
        Layout layout = PatternLayout.newBuilder()
                .withPattern(pattern)
                .build();

        // 创建 ConsoleAppender 并设置布局
        Appender consoleAppender = ConsoleAppender.newBuilder()
                .setLayout(layout)
                .setTarget(ConsoleAppender.Target.SYSTEM_OUT)
                .build();

        // 将 Appender 添加到配置
        DefaultConfiguration configuration = new DefaultConfiguration();
        configuration.addAppender(consoleAppender);
        Configurator.initialize(configuration);

        // 设置日志级别
        Configurator.setRootLevel(org.apache.logging.log4j.Level.DEBUG);


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
