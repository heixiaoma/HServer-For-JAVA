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
        ClassLoader init = JarLoader.init();
        Runner.startMain(() -> {
            try {
                Class<?> mainClass = init.loadClass("org.example.Main");
                System.out.println(mainClass);
                mainClass.getMethod("main", String[].class).invoke(null, (Object) args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }, init);

    }

}
