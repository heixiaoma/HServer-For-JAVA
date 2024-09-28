package cn.hserver.runner;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class JarInfo {

    //胖包 true，还是 轻量包 false
    private boolean type;
    //jar 包是否加密
    private boolean encrypt;
    //jar包路径
    private URL[] libs;

    //真实的启动函数
    private String mainClass;


    public boolean isType() {
        return type;
    }

    public boolean isEncrypt() {
        return encrypt;
    }

    public URL[] getLibs() {
        return libs;
    }

    public String getMainClass() {
        return mainClass;
    }

    public static JarInfo getManifestInfo() {
        try {

            ProtectionDomain protectionDomain = JarInfo.class.getProtectionDomain();
            CodeSource codeSource = protectionDomain.getCodeSource();
            URI location = (codeSource == null ? null : codeSource.getLocation().toURI());
            String path = (location == null ? null : location.getSchemeSpecificPart());

            JarFile jarFile = new JarFile(path);
            Manifest manifest = jarFile.getManifest();
            JarInfo jarInfo = new JarInfo();
            Attributes mainAttribs = manifest.getMainAttributes();
            jarInfo.mainClass = mainAttribs.getValue(RunnerConfig.APP_MAIN_CLASS);
            jarInfo.encrypt = Boolean.parseBoolean(mainAttribs.getValue(RunnerConfig.ENCRYPT));
            jarInfo.type = Boolean.parseBoolean(mainAttribs.getValue(RunnerConfig.TYPE));
            String[] libs = mainAttribs.getValue(RunnerConfig.LIBS).split(",");
            URL[] urlLibs = new URL[libs.length];
            if (jarInfo.type) {
                for (int i = 0; i < libs.length; i++) {
                    urlLibs[i] = new URL(RunnerConfig.IN_JAR + libs[i]);
                }
            } else {
                for (int i = 0; i < libs.length; i++) {
                    urlLibs[i] = new URL(RunnerConfig.OUT_JAR + libs[i]);
                }
            }
            jarInfo.libs = urlLibs;
            jarFile.close();
            return jarInfo;
        } catch (Exception exception) {
            exception. printStackTrace();
            System.exit(-1);
        }
        return null;
    }
}
