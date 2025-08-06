package cn.hserver.plugin.mybatis.flex;

import cn.hserver.core.context.IocApplicationContext;
import cn.hserver.core.plugin.bean.PluginInfo;
import cn.hserver.core.plugin.handler.PluginAdapter;
import cn.hserver.plugin.mybatis.flex.annotation.Mybatis;
import com.mybatisflex.core.MybatisFlexBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

public class MybatisFlexPlugin extends PluginAdapter {
    private static final Logger log = LoggerFactory.getLogger(MybatisFlexPlugin.class);
    private final  Set<Class<?>> annotationList = new HashSet<>();

    @Override
    public PluginInfo getPluginInfo() {
        return new PluginInfo.Builder()
                .name("MyBatis-Flex")
                .description("一个优雅的 MyBatis 增强框架")
                .build();
    }

    @Override
    public void iocStartScan(Class<?> clazz) {
        if (clazz.getAnnotation(Mybatis.class)!=null){
            annotationList.add(clazz);
        }
    }

    @Override
    public void iocStartPopulate() {
        MybatisFlexBootstrap mybatisFlexBootstrap = MybatisFlexConfig.init(annotationList);
        for (Class<?> aClass : annotationList) {
            Object mapper = mybatisFlexBootstrap.getMapper(aClass);
            IocApplicationContext.addBean(mapper);
        }
        annotationList.clear();
    }
}
