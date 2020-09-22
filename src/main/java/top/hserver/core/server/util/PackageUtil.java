package top.hserver.core.server.util;

import top.hserver.core.server.context.ConstConfig;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;

/**
 * 获取最基础的包
 *
 * @author hxm
 */
public class PackageUtil {

    public  static Set<String> scanPackage() {
        /**
         * 把静态文件递归遍历出来.
         */
        //jar的
        if (ConstConfig.RUNJAR) {
            return onlineFile(ConstConfig.CLASSPATH);
        } else {
            //开发中的.看看是不是测试模式
          //测试模式需要把项目路径打包进去
          if (ConstConfig.CLASSPATH.endsWith("test-classes")) {
            Set<String> strings = developFile(ConstConfig.CLASSPATH);
            Set<String> strings1 = developFile(ConstConfig.CLASSPATH.replaceAll("test-classes", "classes"));
            strings.addAll(strings1);
            return strings;
          }else {
            return developFile(ConstConfig.CLASSPATH);
          }
        }
    }

    private  static Set<String> developFile(String path) {
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

    private  static Set<String> onlineFile(String path) {
        Set<String> tmp = new HashSet<>();
        try {
            JarURLConnection jarURLConnection = (JarURLConnection) new URL(path).openConnection();
            JarFile jarFile = jarURLConnection.getJarFile();
            jarFile.stream().forEach(jarEntry -> {
                try {
                    String name = jarEntry.getName();
                    tmp.add(name.substring(0, name.indexOf("/")));
                }catch (Exception ignored){}
            });
            jarFile.close();
        } catch (Exception ignored) {
        }
        return tmp;
    }

}
