package cn.hserver.runner;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

public class JarURLStreamHandlerFactory implements URLStreamHandlerFactory {
    private final ClassLoader classLoader;
    private URLStreamHandlerFactory chainFac;

    public JarURLStreamHandlerFactory(ClassLoader cl) {
        this.classLoader = cl;
    }


    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (RunnerConfig.IN_JAR.equals(protocol + ":"))
            return new JarURLStreamHandler(classLoader, true);
        else if (RunnerConfig.OUT_JAR.equals(protocol + ":")) {
            return new JarURLStreamHandler(classLoader, false);
        }
        if (chainFac != null)
            return chainFac.createURLStreamHandler(protocol);
        return null;
    }

    public void setURLStreamHandlerFactory(URLStreamHandlerFactory fac) {
        chainFac = fac;
    }

}
