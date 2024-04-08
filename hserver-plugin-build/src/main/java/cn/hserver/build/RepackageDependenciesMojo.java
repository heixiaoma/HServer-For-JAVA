package cn.hserver.build;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;

import java.io.File;

@Mojo(name = "repackage", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class RepackageDependenciesMojo extends AbstractMojo {
    @Component
    private MavenProject project;
    private final Log logger = getLog();

    @Override
    public void execute() throws MojoExecutionException {
        try {
            ReBuilderJar reBuilderJar = new ReBuilderJar("test.jar");
            //构建jar包和依赖
            reBuilderJar.buildNewJar(project);
            //copy源码中的依赖
            reBuilderJar.copySource(project);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(),e);
        }
    }
}
