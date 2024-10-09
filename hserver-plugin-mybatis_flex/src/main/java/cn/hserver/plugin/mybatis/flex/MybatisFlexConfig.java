package cn.hserver.plugin.mybatis.flex;

import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.server.context.ConstConfig;
import cn.hserver.core.server.util.JarInputStreamUtil;
import cn.hserver.plugin.mybatis.flex.bean.MybatisConfig;
import com.mybatisflex.core.MybatisFlexBootstrap;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.*;

public class MybatisFlexConfig {


    private static void loadMapperXml(Configuration configuration, String path) {
        Map<String, InputStream> xmlInput = new HashMap<>();
        if (ConstConfig.RUNJAR) {
            onlineFile(ConstConfig.CLASSPATH, path, xmlInput);
        } else {
            developFile(ConstConfig.CLASSPATH + "/" + path, xmlInput);
        }
        xmlInput.forEach((k, v) -> {
            try {
                XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(v, configuration, k, configuration.getSqlFragments());
                xmlMapperBuilder.parse();
            } finally {
                try {
                    v.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private static void developFile(String path, Map<String, InputStream> xmlInput) {
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null != files) {
                File[] var4 = files;
                int var5 = files.length;
                for (int var6 = 0; var6 < var5; ++var6) {
                    File file2 = var4[var6];
                    if (file2.isDirectory()) {
                        developFile(file2.getAbsolutePath(), xmlInput);
                    } else {
                        try {
                            if (file2.getAbsolutePath().endsWith(".xml")) {
                                xmlInput.put(file2.getAbsolutePath(), Files.newInputStream(Paths.get(file2.getAbsolutePath())));
                            }
                        } catch (Exception var9) {
                        }
                    }
                }
            }
        }

    }

    private static void onlineFile(String path, String mapperPath, Map<String, InputStream> xmlInput) {
        try {
            InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            if (resourceAsStream != null) {
                try (JarInputStream jarInputStream = new JarInputStream(JarInputStreamUtil.decrypt(resourceAsStream))) {
                    JarEntry jarEntry;
                    while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
                        String name = jarEntry.getName();
                        if (name.startsWith(mapperPath) && name.endsWith(".xml")) {
                            xmlInput.put(name, MybatisFlexConfig.class.getResourceAsStream("/" + name));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }
    }

    public static MybatisFlexBootstrap init(Set<Class<?>> mappers) {
        MybatisConfig mybatisConfig = IocUtil.getBean(MybatisConfig.class);
        MybatisFlexBootstrap instance = MybatisFlexBootstrap.getInstance();
        //配置数据源
        mybatisConfig.getDataSources().forEach(instance::addDataSource);
        //对mapper进行包装
        mappers.forEach(instance::addMapper);

        MybatisFlexBootstrap start = instance.start();

        Configuration configuration = start.getConfiguration();
        // 拦截器
        Interceptor[] plugins = mybatisConfig.getPlugins();
        if (plugins != null) {
            for (Interceptor plugin : plugins) {
                configuration.addInterceptor(plugin);
            }
        }
        //加载mapper
        loadMapperXml(configuration, mybatisConfig.getMapperLocations());
        return start;
    }

}
