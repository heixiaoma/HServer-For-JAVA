package cn.hserver.build;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.Set;

@Mojo(name = "repackage", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class RepackageDependenciesMojo extends AbstractMojo {
    @Component
    private MavenProject project;
    @Parameter(property = "fatJar", defaultValue = "true")
    private String fatJar;
    @Parameter(property = "password", defaultValue = "")
    private String password;
    private final Log logger = getLog();

    @Override
    public void execute() throws MojoExecutionException {
        try {
            logger.info("是否胖包 :" + fatJar);
            logger.info("加密密钥 :" + password);
            ReBuilderJar reBuilderJar = new ReBuilderJar("test.jar", password,fatJar);
            //构建依赖
            Set<String> dependencies = reBuilderJar.addDependencies(project);
            //copy源码
//            reBuilderJar.copySource(project);
            //设置运行参数
            reBuilderJar.addManifest(project, dependencies);
            reBuilderJar.addRunner();
            reBuilderJar.close();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
    }
}
