package cn.hserver.build;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class ReBuilderJar {

    private String pathJar;

    public ReBuilderJar(String pathJar) {
        this.pathJar = pathJar;
    }

    public void buildNewJar(MavenProject project) throws IOException {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().putValue("Manifest-Version", "1.0");
        manifest.getMainAttributes().putValue("Created-By", "HServer");
        // 创建JAR文件，并传入Manifest
        JarOutputStream jarOutputStream = new JarOutputStream(Files.newOutputStream(Paths.get(pathJar)), manifest);
        // 添加文件到JAR文件
        Set<Artifact> dependencies = project.getArtifacts();
        for (Artifact dependency : dependencies) {
            File sourceFile = dependency.getFile();
            addFileToJar("lib/"+ sourceFile.getName(), sourceFile, jarOutputStream);
        }
        jarOutputStream.close();
    }





    private static void addFileToJar(String entryName, File file, JarOutputStream jarOutputStream) throws IOException {
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

    public void copySource(MavenProject project) throws IOException {
        String artifactPath = project.getBuild().getDirectory() + "/" + project.getBuild().getFinalName() + "." + project.getPackaging();
        // 打开源JAR文件
        JarFile sourceJar = new JarFile(artifactPath);
        // 创建目标JAR文件并传入Manifest
        JarOutputStream targetJarOutputStream = new JarOutputStream(new FileOutputStream(pathJar));

        // 遍历源JAR文件中的每个条目，并将其复制到目标JAR文件中
        Enumeration<JarEntry> entries = sourceJar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();

            // 创建新的JarEntry
            JarEntry newEntry = new JarEntry(entry.getName());
            targetJarOutputStream.putNextEntry(newEntry);

            // 如果是文件，则将内容复制到目标JAR文件中
            if (!entry.isDirectory()&&!entry.getName().contains("META-INF")) {
                FileInputStream fileInputStream = new FileInputStream(entry.getName());
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    targetJarOutputStream.write(buffer, 0, bytesRead);
                }
                fileInputStream.close();
            }
            // 完成当前JarEntry
            targetJarOutputStream.closeEntry();
        }
        // 关闭源JAR文件和目标JAR文件流
        sourceJar.close();
        targetJarOutputStream.close();
    }
}
