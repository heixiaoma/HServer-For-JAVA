package top.hserver.core.server.util;

import top.hserver.core.server.context.ConstConfig;

import java.io.File;
import java.net.URL;

/**
 * @author hxm
 */
public class EnvironmentUtil {

    public static void init(Class clazz) throws Exception {
      /**
       * 测试模式
       */
      if (clazz!=null){
          File f = new File(clazz.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
          ConstConfig.RUNJAR = false;
        /**
         * 静态路径
         */
          ConstConfig.CLASSPATH = f.getPath();
          return;
        }

        /**
         * 运行方式
         */
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        Class<?> aClass=null;
        for (StackTraceElement stackTraceElement : stackTrace) {
            //如果是main
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
        File f = new File(aClass.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
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
