package cn.hserver.runner;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

public class JarURLStreamHandlerFactory implements URLStreamHandlerFactory {
    private final ClassLoader classLoader;
    private URLStreamHandlerFactory chainFac;
    private boolean encrypt;

    public JarURLStreamHandlerFactory(ClassLoader cl) {
        this.classLoader = cl;
    }

    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }

    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (RunnerConfig.IN_JAR.equals(protocol + ":"))
            return new JarURLStreamHandler(classLoader, true, encrypt);
        else if (RunnerConfig.OUT_JAR.equals(protocol + ":")) {
            return new JarURLStreamHandler(classLoader, false, encrypt);
        }
        if (chainFac != null)
            return chainFac.createURLStreamHandler(protocol);
        return null;
    }

    public void setURLStreamHandlerFactory(URLStreamHandlerFactory fac) {
        chainFac = fac;
    }

}
