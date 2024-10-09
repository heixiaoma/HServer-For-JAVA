package cn.hserver.plugins.maven;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.CipherInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.jar.*;

public class JarUtil {
    public static boolean alreadyPackage(File file) {
        try {
            JarFile jarFile=new JarFile(file);
            Manifest manifest = jarFile.getManifest();
            Attributes mainAttribs = manifest.getMainAttributes();
            String value = mainAttribs.getValue("Created-By");
            if (StringUtils.isNotEmpty(value) && value.equals("HServer")) {
                jarFile.close();
                return true;
            }
            jarFile.close();
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static void addFileToLibs(String entryName, File file, String password) throws Exception {
        Path path = Paths.get(entryName);
        Path parent = path.getParent();
        if (!parent.toFile().isDirectory()) {
            parent.toFile().mkdir();
        }
        if (StringUtils.isNotEmpty(password)) {
            if (Files.exists(path)) {
                Files.delete(path);
            }
            FileInputStream fileInputStream = new FileInputStream(file);
            CipherInputStream encrypt = AesUtil.encrypt(fileInputStream, password);
            Files.copy(encrypt, path);
            encrypt.close();
            fileInputStream.close();
        } else {
            if (Files.exists(path)) {
                Files.delete(path);
            }
            Files.copy(file.toPath(), path);
        }
    }

    public static void addFileToJar(String entryName, File file, JarOutputStream jarOutputStream, String password) throws Exception {
        // 创建新的JarEntry，指定添加到JAR文件的目录和文件名
        JarEntry jarEntry = new JarEntry(entryName);
        jarOutputStream.putNextEntry(jarEntry);
        // 读取文件内容并写入到JAR文件
        FileInputStream fileInputStream = new FileInputStream(file);
        if (StringUtils.isNotEmpty(password)) {
            CipherInputStream encrypt = AesUtil.encrypt(fileInputStream, password);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = encrypt.read(buffer)) != -1) {
                jarOutputStream.write(buffer, 0, bytesRead);
            }
            encrypt.close();
        } else {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                jarOutputStream.write(buffer, 0, bytesRead);
            }
        }
        // 关闭文件输入流
        fileInputStream.close();
        // 完成当前JarEntry
        jarOutputStream.closeEntry();
    }


    public static void copyJarEntries(String sourceJarPath, JarOutputStream targetJar) throws IOException {
        try (JarFile sourceJar = new JarFile(sourceJarPath)) {
            Enumeration<JarEntry> entries = sourceJar.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (jarEntry.getName().contains("META-INF")) {
                    continue;
                }
                JarEntry newEntry = new JarEntry(jarEntry.getName());
                try {
                    targetJar.putNextEntry(newEntry);
                }catch (Exception e){
                    continue;
                }
                // 将源 jar 文件中的内容写入目标 jar 文件
                InputStream entryInputStream = sourceJar.getInputStream(jarEntry);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = entryInputStream.read(buffer)) != -1) {
                    targetJar.write(buffer, 0, bytesRead);
                }
                // 关闭当前条目
                targetJar.closeEntry();
                entryInputStream.close();
            }
        }
    }


    public static String getMainClassName(String jarPath) throws IOException {
        File f = new File(jarPath);
        URL url1 = f.toURI().toURL();
        URLClassLoader myClassLoader = new URLClassLoader(new URL[]{url1}, Thread.currentThread().getContextClassLoader());
        JarFile jar = new JarFile(jarPath);
        Enumeration<JarEntry> enumFiles = jar.entries();
        ClassPool classPool = ClassPool.getDefault();
        while (enumFiles.hasMoreElements()) {
            JarEntry entry = enumFiles.nextElement();
            String classFullName = entry.getName();

            if (classFullName.endsWith(".class")) {
                try {
                    InputStream inputStream = jar.getInputStream(entry);
                    CtClass ctClass = classPool.makeClass(inputStream);
                    inputStream.close();
                    CtMethod[] methods = ctClass.getDeclaredMethods();
                    for (CtMethod method : methods) {
                        if (method.getName().equals("main") && method.getParameterTypes().length == 1) {
                            if (method.getParameterTypes()[0].getName().equals("java.lang.String[]") && Modifier.isStatic(method.getModifiers())) {
                                if (method.getReturnType().getName().equals("void")) {
                                    if (ctClass.hasAnnotation("cn.hserver.core.ioc.annotation.HServerBoot")){
                                        jar.close();
                                        return ctClass.getName();
                                    }
                                }
                            }
                        }
                    }
                } catch (Throwable ignored) {
                    ignored.printStackTrace();
                }
            }
        }
        jar.close();
        myClassLoader.close();
        throw new IllegalStateException("找不到启动类,请使用@HServerBoot标记你的启动类");
    }
}
