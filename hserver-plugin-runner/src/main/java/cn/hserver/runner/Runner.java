package cn.hserver.runner;

import java.io.Console;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class Runner {

    public static String password;


    private static void checkPassword(JarInfo manifestInfo) {
        if (manifestInfo.isEncrypt()) {
            //jar 参数读取
            password = System.getProperty("password");
            if (password == null || password.trim().isEmpty()) {
                Console console = System.console();
                // 读取密码
                char[] passwordArray = console.readPassword("请输入运行密码: ");
                password = new String(passwordArray);
                if (password.trim().isEmpty()) {
                    System.exit(-1);
                }else {
                    System.setProperty("password",password);
                }
            }
            System.out.println("运行密码: "+ password);
        }
    }


    public static void main(String[] args) throws Exception {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        URL.setURLStreamHandlerFactory(new JarURLStreamHandlerFactory(contextClassLoader));
        JarInfo manifestInfo = JarInfo.getManifestInfo();
        checkPassword(manifestInfo);
        ClassLoader classLoader = new URLClassLoader(manifestInfo.getLibs(), contextClassLoader);
        Thread.currentThread().setContextClassLoader(classLoader);
        Class<?> c = Class.forName(manifestInfo.getMainClass(), true, classLoader);
        Method main = c.getMethod("main", args.getClass());
        main.invoke(null, new Object[]{args});
    }

}
