package cn.hserver.core.util;


import cn.hserver.core.config.ConstConfig;

import java.io.File;
import java.net.URI;
import java.security.CodeSource;
import java.security.ProtectionDomain;

/**
 * @author hxm
 */
public class EnvironmentUtil {

    public static void init(Class<?> clazz)  {
        try {
        /*
          测试模式
         */
            if (clazz != null) {
                    File f = new File(clazz.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
                    ConstConfig.RUN_JAR = false;
            /*
              静态路径
             */
                    ConstConfig.CLASSPATH = f.getPath();
                    return;
            }
        /*
          运行方式
         */
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            Class<?> aClass = null;
            for (StackTraceElement stackTraceElement : stackTrace) {
                //如果是main
                if ("main".equals(stackTraceElement.getMethodName())) {
                    try {
                        aClass = Class.forName(stackTraceElement.getClassName());
                        break;
                    } catch (Exception e) {
                        return;
                    }
                }
            }
            if (aClass == null) {
                return;
            }

            ProtectionDomain protectionDomain = aClass.getProtectionDomain();
            CodeSource codeSource = protectionDomain.getCodeSource();
            URI location = (codeSource == null ? null : codeSource.getLocation().toURI());
            String path = (location == null ? null : location.getSchemeSpecificPart());
            if (path != null && (path.endsWith(".jar") || path.endsWith(".jar!/"))) {
                ConstConfig.RUN_JAR = true;
                ConstConfig.CLASSPATH = path;
            } else {
                ConstConfig.CLASSPATH = new File(aClass.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getPath();
                ConstConfig.RUN_JAR = false;
            }
        } catch (Exception e) {
                throw new RuntimeException(e);
        }
    }

}
