package test3;

import top.hserver.core.server.handlers.StaticHandler;
import top.hserver.core.server.util.EnvironmentUtil;
import top.hserver.core.server.util.JvmStack;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class test {
    public static void main(String[] args) {
//        EnvironmentUtil.init();
        onlineFile("D:\\Java\\campus\\admin\\target\\hserver-for-java-system.jar");
    }


    private static Set<String> onlineFile(String path) {
        HashSet tmp = new HashSet();

        try {
            JarFile jarFile = new JarFile(path);
            jarFile.stream().forEach((jarEntry) -> {
                String name = jarEntry.getName();
                System.out.println(name);
                tmp.add(name.substring(0, name.indexOf("/")));
            });
            jarFile.close();
        } catch (Exception var4) {
        }

        return tmp;
    }


}
