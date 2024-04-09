package cn.hserver.runner;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class JarLoader extends URLClassLoader {
    public JarLoader(URL[] urls, ClassLoader classLoader) {
        super(urls, classLoader);
    }

    private JarLoader(URL[] urls) {
        super(urls);
    }

    private JarLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }


    @Override
    protected Package definePackage(String name, Manifest man, URL url) throws IllegalArgumentException {
        System.out.println("1-->"+name + man + url);
        return super.definePackage(name, man, url);
    }


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        System.out.println("2-->"+name);
        return super.findClass(name);
    }

    @Override
    public URL findResource(String name) {
        System.out.println("3-->"+name);
        return super.findResource(name);
    }

    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
        System.out.println("4-->"+name);
        return super.findResources(name);
    }




    /**
     * 添加jar里的jar
     *
     * @param path
     */
    public static List<URL> getJarInJar(String path) {
        List<URL> urls = new ArrayList<>();
        if (path.endsWith(".jar")) {
            try {
                // 使用JarFile解析外部JAR文件
                JarFile externalJarFile = new JarFile(path);
                for (JarEntry entry : Collections.list(externalJarFile.entries())) {
                    // 如果entry是JAR文件，并且不是外部JAR文件本身
                    if (entry.getName().endsWith(".jar") && !entry.getName().equals(path)) {
                        // 获取内部JAR文件的路径
                        String internalJarPath = entry.getName();
                        // 将内部JAR文件的路径添加到URLClassLoader的搜索路径中
                        System.out.println("jar:file:" + path + "!/" + internalJarPath);
                        urls.add(new URL("jar:file:" + path + "!/" + internalJarPath));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("path is not a jar file");
            System.exit(-1);
        }
        return urls;
    }
}
