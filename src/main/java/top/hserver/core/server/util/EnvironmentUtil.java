package top.hserver.core.server.util;

import top.hserver.core.server.context.ConstConfig;

import java.io.File;
import java.net.URL;

/**
 * @author hxm
 */
public class EnvironmentUtil {

    public static void init() {
        /**
         * 运行方式
         */
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        Class<?> aClass=null;
        for (StackTraceElement stackTraceElement : stackTrace) {
            //如果是主函数
            if ("main".equals(stackTraceElement.getMethodName())){
                try {
                    aClass = Class.forName(stackTraceElement.getClassName());
                    break;
                }catch (Exception e){
                    return;
                }
            }
        }
        if (aClass==null){
            return;
        }
        File f = new File(aClass.getProtectionDomain().getCodeSource().getLocation().getPath());
        if (!f.getPath().endsWith(".jar")) {
            ConstConfig.RUNJAR = false;
        } else {
            ConstConfig.RUNJAR = true;
        }
        /**
         * 静态路径
         */
        ConstConfig.CLASSPATH = f.getPath();
    }

}
