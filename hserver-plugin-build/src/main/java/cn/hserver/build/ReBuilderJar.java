package cn.hserver.build;

import cn.hserver.runner.Runner;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.*;

public class ReBuilderJar {
    private final JarOutputStream jarOutputStream;

    public ReBuilderJar(String pathJar) throws IOException {
        this.jarOutputStream = new JarOutputStream(Files.newOutputStream(Paths.get(pathJar)));
    }

    public Set<String> buildNewJar(MavenProject project) throws IOException {
        Set<String> dependencies = new LinkedHashSet<>();
//        dependencies.add("./");
//        dependencies.add("./lib");
        Set<Artifact> artifacts = project.getArtifacts();
        for (Artifact dependency : artifacts) {
            File sourceFile = dependency.getFile();
            String s = JarUtil.addFileToJar("lib/" + sourceFile.getName().trim(), sourceFile, jarOutputStream);
            dependencies.add(s);
        }
        String artifactPath = project.getBuild().getDirectory() + "/" + project.getBuild().getFinalName() + "." + project.getPackaging();
        String s = JarUtil.addFileToJar("lib/" + project.getBuild().getFinalName() + "." + project.getPackaging(), new File(artifactPath), jarOutputStream);
        dependencies.add(s);
        return dependencies;
    }

    public void addManifest(MavenProject project, Set<String> dependencies) throws IOException {
        String artifactPath = project.getBuild().getDirectory() + "/" + project.getBuild().getFinalName() + "." + project.getPackaging();
        String mainClassName = JarUtil.getMainClassName(artifactPath);
        jarOutputStream.putNextEntry(new JarEntry("META-INF/MANIFEST.MF"));
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().putValue("Manifest-Version", "1.0");
        manifest.getMainAttributes().putValue("Created-By", "HServer");
        manifest.getMainAttributes().putValue("App-Main-Class", mainClassName);
        manifest.getMainAttributes().putValue("Type", "true");
        manifest.getMainAttributes().putValue("Libs",String.join(",", dependencies));
        manifest.getMainAttributes().putValue("Main-Class", "cn.hserver.runner.Runner");
        manifest.getMainAttributes().putValue("Class-Path", ".");
        // 写入 Manifest 内容
        manifest.write(new BufferedOutputStream(jarOutputStream));
    }


    public void copySource(MavenProject project) throws IOException {
        String artifactPath = project.getBuild().getDirectory() + "/" + project.getBuild().getFinalName() + "." + project.getPackaging();
        JarUtil.copyJarEntries(artifactPath, jarOutputStream);
    }


    public void addRunner() throws IOException, URISyntaxException {
        ProtectionDomain protectionDomain = Runner.class.getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URI location = (codeSource == null ? null : codeSource.getLocation().toURI());
        String path = (location == null ? null : location.getSchemeSpecificPart());
        if (path != null && (path.endsWith(".jar") || path.endsWith(".jar!/"))) {
            String s = path.replaceAll("file:", "").replaceAll("!/", "");
            JarUtil.copyJarEntries(s, jarOutputStream);
        }
    }

    public void close() throws IOException {
        jarOutputStream.close();
    }
}
