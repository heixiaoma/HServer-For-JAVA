package top.hserver.core.log;

import ch.qos.logback.classic.LoggerContext;
import org.slf4j.impl.StaticLoggerBinder;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author hxm
 */
public class HServerLogConfig {

    private static String[] getStandardConfigLocations() {
        return new String[]{"logback-test.groovy", "logback-test.xml", "logback.groovy", "logback.xml"};
    }

    public static void init() {
        try {
            if (existConfig()){
                return;
            }
            loadConfiguration(HServerLogConfig.class.getResourceAsStream("/logback-hserver.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean existConfig(){
        for (String s : getStandardConfigLocations()) {
            InputStream resourceAsStream = HServerLogConfig.class.getResourceAsStream("/" + s);
            if (resourceAsStream!=null){
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
        LoggerContext loggerContext = (LoggerContext)StaticLoggerBinder.getSingleton().getLoggerFactory();
        stopAndReset(loggerContext);
        ch.qos.logback.classic.joran.JoranConfigurator configurator = new ch.qos.logback.classic.joran.JoranConfigurator();
        configurator.setContext(loggerContext);
        configurator.doConfigure(in);
        in.close();
    }

}
