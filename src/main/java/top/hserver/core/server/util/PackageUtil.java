package top.hserver.core.server.util;

import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.handlers.StaticHandler;

import java.io.File;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 获取最基础的包
 *
 * @author hxm
 */
public class PackageUtil {

    public static Set<String> scanPackage() {
        /**
         * 把静态文件递归遍历出来.
         */
        //jar的
        if (ConstConfig.RUNJAR) {
            return onlineFile(ConstConfig.CLASSPATH);
        } else {
            //开发中的.
            return developFile(ConstConfig.CLASSPATH);
        }
    }

    private static Set<String> developFile(String path) {
        Set<String> tmp = new HashSet<>();
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null != files) {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        tmp.add(file2.getPath().substring(path.length() + 1));
                    }
                }
            }
        }
        return tmp;
    }

    private static Set<String> onlineFile(String path) {
        Set<String> tmp = new HashSet<>();
        try {
            JarFile jarFile = new JarFile(path);
            Enumeration<JarEntry> entry = jarFile.entries();
            while (entry.hasMoreElements()) {
                JarEntry jar = entry.nextElement();
                String name = jar.getName();
                tmp.add(name.substring(0, name.indexOf("/")));
            }
            jarFile.close();
        } catch (Exception ignored) {
        }
        return tmp;
    }

}
