package cn.hserver.runner;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class StartRunner {


    public static void main(String[] args) throws Exception {
        // 获取当前JAR文件的URL
        URL jarFileUrl = StartRunner.class.getProtectionDomain().getCodeSource().getLocation();

        // 获取lib文件夹下所有JAR文件的URL
        List<URL> libURLs = getLibJarURLs(jarFileUrl);

        // 创建一个新的URLClassLoader，将lib文件夹下的JAR文件添加到父ClassLoader中
        URLClassLoader classLoader = new URLClassLoader(libURLs.toArray(new URL[0]), StartRunner.class.getClassLoader());

        // 加载并执行您的主类
        Class<?> mainClass = classLoader.loadClass("cn.hserver.HServerApplication");
        System.out.println(mainClass);
        mainClass.getMethod("main", String[].class).invoke(null, (Object) args);
    }

    // 获取lib文件夹下所有JAR文件的URL
    private static List<URL> getLibJarURLs(URL jarFileUrl) throws IOException {
        List<URL> libURLs = new ArrayList<>();
        JarFile externalJarFile = new JarFile(jarFileUrl.getPath());
        for (JarEntry entry : Collections.list(externalJarFile.entries())) {
            // 如果entry是JAR文件，并且不是外部JAR文件本身
            if (entry.getName().endsWith(".jar") ) {
                String internalJarPath = entry.getName();
                System.out.println(internalJarPath);
            }
        }
        return libURLs;
    }

//
//    public static void main(String[] args) throws Exception{
//        URL url = new URL("jar:file:/Users/heixiaoma/Code/java/hserver_demo/hserver_demo/test.jar!/lib/hserver-3.5.M1.jar");
//        // 将URL添加到URL数组中
//        // 此处您可以根据需要将URL添加到列表中，然后将列表转换为URL数组
//        // 这里为了简单起见，直接创建一个长度为1的数组
//        URL[] urls = new URL[]{url};
//
//        // 创建一个新的URLClassLoader，以加载lib文件夹下的JAR文件
//        ClassLoader classLoader = new URLClassLoader(urls, StartRunner.class.getClassLoader());
//
//        // 加载并执行您的主类
//        Class<?> mainClass = classLoader.loadClass("cn.hserver.HServerApplication");
//        System.out.println(mainClass);
//
//
//    }
}
