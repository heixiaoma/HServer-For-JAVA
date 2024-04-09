package cn.hserver.runner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import sun.net.www.protocol.jar.JarURLConnection;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class TestStartRunner {

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
    private static ArrayList<URL> getLibJarURLs(URL jarFileUrl) throws IOException {
        ArrayList<URL> libURLs = new ArrayList<>();
        JarFile jarFile = new JarFile(jarFileUrl.getPath());
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (entry.getName().startsWith("lib/") && entry.getName().endsWith(".jar")) {
                // 读取JAR文件内容并写入ByteArrayOutputStream
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                try (InputStream inputStream = jarFile.getInputStream(entry)) {
                    byte[] data = new byte[1024];
                    int nRead;
                    while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }
                    buffer.flush();
                }
                // 创建URL并添加到列表中
                URL url = new URL("jar:" + jarFileUrl + "!/" + entry.getName());
                libURLs.add(url);
            }
        }
        jarFile.close();
        return libURLs;
    }
}
