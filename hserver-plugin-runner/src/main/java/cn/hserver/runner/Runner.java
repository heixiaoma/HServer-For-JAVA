package cn.hserver.runner;

import java.io.Console;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Scanner;

public class Runner {

    public static String password;


    private static void checkPassword(JarInfo manifestInfo) {
        if (manifestInfo.isEncrypt()) {
            if (password == null || password.trim().isEmpty()) {
                if (manifestInfo.getConsole()){
                    Console console = System.console();
                    char[] passwordArray = console.readPassword();
                    password = new String(passwordArray);
                }else {
                    Scanner scanner = new Scanner(System.in);
                    password= scanner.nextLine();
                }
                if (password.trim().isEmpty()) {
                    System.exit(-1);
                }
            }
        }
    }


    public static void main(String[] args) throws Exception {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            URL.setURLStreamHandlerFactory(new JarURLStreamHandlerFactory(contextClassLoader));
            JarInfo manifestInfo = JarInfo.getManifestInfo();
            checkPassword(manifestInfo);
            ClassLoader classLoader = new URLClassLoader(manifestInfo.getLibs(), contextClassLoader);
            Thread.currentThread().setContextClassLoader(classLoader);
            Class<?> conf = Class.forName("cn.hserver.core.server.context.ConstConfig", true, classLoader);
            Field field = conf.getDeclaredField("PASSWORD");
            field.set(null, password);
            Class<?> main = Class.forName(manifestInfo.getMainClass(), true, classLoader);
            Method method = main.getMethod("main", args.getClass());
            method.invoke(null, new Object[]{args});
    }

}
