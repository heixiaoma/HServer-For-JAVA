package cn.hserver.runner;

import java.net.*;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarLoader {

    public static URL[] urls;

    /**
     * 添加jar里的jar
     */
    public static ClassLoader init() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL.setURLStreamHandlerFactory(new JarInJarURLStreamHandlerFactory(cl));
        try {
            ProtectionDomain protectionDomain = JarLoader.class.getProtectionDomain();
            CodeSource codeSource = protectionDomain.getCodeSource();
            URI location = (codeSource == null ? null : codeSource.getLocation().toURI());
            String path = (location == null ? null : location.getSchemeSpecificPart());
            if (path == null) {
                throw new IllegalStateException("Unable to determine code source archive");
            }
            List<URL> urlList = new ArrayList<>();
            JarFile externalJarFile = new JarFile(path);
            for (JarEntry entry : Collections.list(externalJarFile.entries())) {
                if (entry.getName().endsWith(".jar") && !entry.getName().equals(path)) {
                    String internalJarPath = entry.getName();
                    urlList.add(new URL("jarinjar:file:" + path + "!/" + internalJarPath + "!/"));
                }
            }
            urls = urlList.toArray(new URL[0]);
            Thread.currentThread().setContextClassLoader(new URLClassLoader(urls, cl));
            return Thread.currentThread().getContextClassLoader();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }
}
