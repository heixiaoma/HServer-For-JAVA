package cn.hserver.build;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class JarUtil {




    public static void addFileToJar(String entryName, File file, JarOutputStream jarOutputStream) throws IOException {
        // 创建新的JarEntry，指定添加到JAR文件的目录和文件名
        JarEntry jarEntry = new JarEntry(entryName);
        jarOutputStream.putNextEntry(jarEntry);

        // 读取文件内容并写入到JAR文件
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            jarOutputStream.write(buffer, 0, bytesRead);
        }

        // 关闭文件输入流
        fileInputStream.close();

        // 完成当前JarEntry
        jarOutputStream.closeEntry();
    }


    public static void copyJarEntries(String sourceJarPath, String targetJarPath) throws IOException {
        try (JarFile sourceJar = new JarFile(sourceJarPath);
             JarOutputStream targetJarOutputStream = new JarOutputStream(new FileOutputStream(targetJarPath), sourceJar.getManifest())) {
            Enumeration<JarEntry> entries = sourceJar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().startsWith("META-INF/") || entry.isDirectory()){
                    continue;
                }
                // 创建新的JarEntry
                JarEntry newEntry = new JarEntry(entry.getName());
                targetJarOutputStream.putNextEntry(newEntry);

                // 如果是文件，则使用NIO进行复制
                if (!entry.isDirectory()) {
                    try (FileSystem sourceFs = FileSystems.newFileSystem(Paths.get(sourceJarPath), null);
                         FileSystem targetFs = FileSystems.newFileSystem(Paths.get(targetJarPath), null)) {

                        Path sourcePath = sourceFs.getPath("/" + entry.getName());
                        Path targetPath = targetFs.getPath("/" + entry.getName());

                        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }

                // 完成当前JarEntry
                targetJarOutputStream.closeEntry();
            }
        }
    }
}
