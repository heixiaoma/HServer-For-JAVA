package cn.hserver.runner;

import java.net.MalformedURLException;

public class TestStartRunner {
    public static void main(String[] args) throws MalformedURLException {
        JarLoader jarLoader = new JarLoader();
        jarLoader.addJarInJar("/Users/heixiaoma/Code/java/hserver_demo/hserver_demo/target/hserver_demo-1.0-SNAPSHOT.jar");
        new Runner().startMain(() -> {
            // 这里可以写你的启动代码
            Class<?> aClass = null;
            try {
                aClass = jarLoader.loadClass("org.example.Main");
                aClass.getMethod("main", String[].class).invoke(null, (Object) new String[]{});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, jarLoader);
    }
}
