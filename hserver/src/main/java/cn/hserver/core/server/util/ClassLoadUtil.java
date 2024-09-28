package cn.hserver.core.server.util;


import javassist.ClassPool;
import javassist.Loader;
import javassist.LoaderClassPath;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * class加载工具类
 *
 * @author hxm
 */
public class ClassLoadUtil {

    private static ClassLoader classLoader;

    /**
     * 加载包下的class
     *
     * @param packageName 包名，如：com
     * @return Class列表
     */
    public static List<Class<?>> LoadClasses(final String packageName, boolean isJavassist) {
        List<Class<?>> classes = new ArrayList<>();
        String packageDirName = packageName.replace('.', '/');
        try {
            if (isJavassist) {
                ClassPool cp = ClassPool.getDefault();
                Loader loader = new Loader(Thread.currentThread().getContextClassLoader(), cp);
                loader.delegateLoadingOf("jdk.internal.reflect.");
                classLoader = loader;
            } else {
                classLoader = Thread.currentThread().getContextClassLoader();
            }
            Enumeration<URL> dirs = classLoader.getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findAndLoadClassesInPackageByFile(packageName, filePath, classes);
                } else if ("jar".equals(protocol)) {
                    JarFile jar;
                    try {
                        String tmpPackage;
                        jar = ((java.net.JarURLConnection) url.openConnection()).getJarFile();
                        Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            if (name.charAt(0) == '/') {
                                name = name.substring(1);
                            }
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                if (idx != -1) {
                                    tmpPackage = name.substring(0, idx).replace('/', '.');
                                } else {
                                    tmpPackage = packageName;
                                }
                                if (name.endsWith(".class") && !entry.isDirectory()) {
                                    String className = name.substring(tmpPackage.length() + 1, name.length() - 6);
                                    try {
                                        classes.add(classLoader.loadClass(tmpPackage + '.' + className));
                                    } catch (Throwable e) {
                                        if (!"HServerTest".equals(className)) {
//                                                log.error(e.getMessage());
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Throwable e) {
//                        log.error(e.getMessage());
                    }
                }
            }
        } catch (Throwable e) {
//            log.error(e.getMessage());
        }

        return classes;
    }

    private static void findAndLoadClassesInPackageByFile(String packageName, String packagePath, List<Class<?>> classes) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirfiles = dir.listFiles(file -> (file.isDirectory()) || (file.getName().endsWith(".class")));
        assert dirfiles != null;
        for (File file : dirfiles) {
            if (file.isDirectory()) {
                findAndLoadClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), classes);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    classes.add(classLoader.loadClass(packageName + '.' + className));
                } catch (Throwable e) {
//                    log.error(e.getMessage());
                }
            }
        }
    }
}
