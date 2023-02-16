package cn.hserver.plugins.maven;


import cn.hserver.plugin.loader.MainMethodRunner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

/**
 * @author hxm
 */
public class CopyLoader {

    public static void start(File file) throws Exception {
        //获取classloader路径
        ProtectionDomain protectionDomain = MainMethodRunner.class.getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URI location = (codeSource == null ? null : codeSource.getLocation().toURI());
        String path = (location == null ? null : location.getSchemeSpecificPart());
        if (path != null && (path.endsWith(".jar") || path.endsWith(".jar!/"))) {
            String s = path.replaceAll("file:", "").replaceAll("!/", "");
            updateJarFile(file,new File(s));
        }
    }

    public static void updateJarFile(File srcJarFile,File destJarFile) throws IOException {
        File tmpJarFile = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
        JarFile jarFile = new JarFile(srcJarFile);
        boolean jarUpdated = false;

        try {
            JarOutputStream tempJarOutputStream = new JarOutputStream(new FileOutputStream(tmpJarFile));
            try {
                // 写classloader
                JarFile classLoaderJarfile = new JarFile(destJarFile);
                Enumeration<JarEntry> entries = classLoaderJarfile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    try {
                        if (!jarEntry.isDirectory() && jarEntry.getName().contains("cn/hserver/plugin/loader/") && jarEntry.getName().endsWith(".class")) {
                            tempJarOutputStream.putNextEntry(jarEntry);
                            tempJarOutputStream.write(IOUtils.toByteArray(classLoaderJarfile.getInputStream(jarEntry)));
                        }
                    }catch (Exception ignored){}
                }
                // 源 项目文件
                Enumeration<?> jarEntries = jarFile.entries();
                while (jarEntries.hasMoreElements()) {
                    try {
                        JarEntry entry = (JarEntry) jarEntries.nextElement();
                        tempJarOutputStream.putNextEntry(entry);
                        InputStream entryInputStream = jarFile
                                .getInputStream(entry);
                        tempJarOutputStream.write(IOUtils.toByteArray(entryInputStream));
                    }catch (Exception ignored){
                    }
                }
                jarUpdated = true;
            } catch (Exception ex) {
                tempJarOutputStream.putNextEntry(new JarEntry("stub"));
            } finally {
                tempJarOutputStream.close();
            }

        } finally {
            jarFile.close();
            if (!jarUpdated) {
                tmpJarFile.delete();
            }
        }

        if (jarUpdated) {
            srcJarFile.delete();
            FileUtils.moveFile(tmpJarFile,srcJarFile);
        }
    }
}
