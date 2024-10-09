package cn.hserver.plugins.maven;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;

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
            //检查是否是已经打包过了
            if (JarUtil.alreadyPackage(project.getArtifact().getFile())) {
                logger.info("已经打包");
                return;
            }
            long startTime = System.currentTimeMillis();
            logger.info("是否胖包 :" + fatJar);
            if (StringUtils.isNotEmpty(password)) {
                logger.info("加密密钥 :" + password);
            }
            String targetPath = project.getArtifact().getFile().getParent();
            ReBuilderJar reBuilderJar = new ReBuilderJar(targetPath, password, fatJar);
            //构建依赖
            Set<String> dependencies = reBuilderJar.addDependencies(project);
            //copy源码
//            reBuilderJar.copySource(project);
            //设置运行参数
            reBuilderJar.addManifest(project, dependencies);
            reBuilderJar.addRunner();
            reBuilderJar.close();
            reBuilderJar.rename(project);
            logger.info("耗时:" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
    }
}
