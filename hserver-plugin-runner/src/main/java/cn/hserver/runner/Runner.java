package cn.hserver.runner;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class Runner {

    public static void main(String[] args) throws Exception{
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        URL.setURLStreamHandlerFactory(new JarURLStreamHandlerFactory(contextClassLoader));
        JarInfo manifestInfo = JarInfo.getManifestInfo();
        ClassLoader jceClassLoader = new URLClassLoader(manifestInfo.getLibs(), null);
        Thread.currentThread().setContextClassLoader(jceClassLoader);
        Class<?> c = Class.forName(manifestInfo.getMainClass(), true, jceClassLoader);
        Method main = c.getMethod("main", args.getClass());
        main.invoke(null, new Object[]{args});

    }

}
