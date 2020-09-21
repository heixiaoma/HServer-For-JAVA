package top.hserver.core.loader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.Manifest;

/**
 * @author hxm
 */
public class HServerJarMainClassStart {
    public static void main(String[] args) throws Exception {
        URLClassLoader classLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
        classLoader.getResources("META-INF/MANIFEST.MF");
        Enumeration<URL> urls = classLoader.getResources("META-INF/MANIFEST.MF");
        if (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            Manifest manifest = new Manifest(url.openStream());
            String startClass = manifest.getMainAttributes().getValue("Start-Class");
            MainMethodRunner mainMethodRunner = new MainMethodRunner(startClass, args);
            mainMethodRunner.run();
        }
    }

}
