package top.hserver.core.server.util;

import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.handlers.StaticHandler;

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
        URL resource = Thread.currentThread().getClass().getResource("/");
        if ("file".equals(resource.getProtocol())) {
            ConstConfig.RUNJAR = false;
        } else {
            ConstConfig.RUNJAR = true;
        }
        System.out.println(resource.getPath());
        /**
         * 静态路径
         */
        ConstConfig.CLASSPATH = resource.getPath();
    }

}
