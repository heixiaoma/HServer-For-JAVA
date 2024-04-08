package cn.hserver.build;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
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
            JarUtil.addFileToJar("lib/"+ sourceFile.getName(), sourceFile, jarOutputStream);
        }
        jarOutputStream.close();
    }



    public void copySource(MavenProject project) throws IOException {
        String artifactPath = project.getBuild().getDirectory() + "/" + project.getBuild().getFinalName() + "." + project.getPackaging();
        JarUtil.copyJarEntries(artifactPath,pathJar);
    }
}
