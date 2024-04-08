package cn.hserver.runner;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarLoader extends URLClassLoader {
    public JarLoader() {
        super(new URL[]{}, Thread.currentThread().getContextClassLoader());
    }

    private JarLoader(URL[] urls) {
        super(urls);
    }

    private JarLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }


    /**
     * 添加jar里的jar
     *
     * @param path
     */
    public void addJarInJar(String path) {
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
                        super.addURL(new URL("jar:file:" + path + "!/" + internalJarPath));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("path is not a jar file");
            System.exit(-1);
        }
    }
}
