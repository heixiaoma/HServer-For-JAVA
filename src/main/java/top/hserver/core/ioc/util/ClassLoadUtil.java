package top.hserver.core.ioc.util;


import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * class加载工具类
 */
@Slf4j
public class ClassLoadUtil {

    /**
     * 加载包下的class
     *
     * @param packageName 包名，如：com.xxx.yyy
     * @param recursive   是否要递归加载子包下的class
     * @return Class列表
     */
    public static List<Class<?>> LoadClasses(String packageName, boolean recursive) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        String packageDirName = packageName.replace('.', '/');
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> dirs = classLoader.getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findAndLoadClassesInPackageByFile(classLoader, packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    JarFile jar;
                    try {
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
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                if ((idx != -1) || recursive) {
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            classes.add(classLoader.loadClass(packageName + '.' + className));
                                        } catch (Throwable e) {
                                            log.error(e.getMessage());
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return classes;
    }

    private static void findAndLoadClassesInPackageByFile(ClassLoader classLoader, String packageName, String packagePath,
                                                          final boolean recursive, List<Class<?>> classes) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirfiles = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        for (File file : dirfiles) {
            if (file.isDirectory()) {
                findAndLoadClassesInPackageByFile(classLoader, packageName + "." + file.getName(), file.getAbsolutePath(), recursive,
                        classes);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    classes.add(classLoader.loadClass(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    public static List<Class> getAllClassByInterface(Class c) {
        // 返回结果
        List<Class> returnClassList = new ArrayList<Class>();
        // 如果不是一个接口，则不做处理
        if (c.isInterface()) {
            // 获得当前的包名
            String packageName = c.getPackage().getName();
            try {
                // 获得当前包下以及子包下的所有类
                List<Class> allClass = getClasses(packageName);
                // 判断是否是同一个接口
                for (int i = 0; i < allClass.size(); i++) {
                    // 判断是不是一个接口
                    if (c.isAssignableFrom(allClass.get(i))) {
                        // 本身不加进去
                        if (!c.equals(allClass.get(i))) {
                            returnClassList.add(allClass.get(i));
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return returnClassList;
    }


    private static List<Class> getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread()
                .getContextClassLoader();
        // 用'/'代替'.'路径
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }

    private static List<Class> findClasses(File directory, String packageName)
            throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file,
                        packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                // 去掉'.class'
                classes.add(Class.forName(packageName
                        + '.'
                        + file.getName().substring(0,
                        file.getName().length() - 6)));

            }
        }
        return classes;
    }


}
