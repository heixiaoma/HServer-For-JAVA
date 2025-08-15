package cn.hserver.plugin.mybatis;

import cn.hserver.core.context.IocApplicationContext;
import cn.hserver.core.plugin.bean.PluginInfo;
import cn.hserver.core.plugin.handler.PluginAdapter;
import cn.hserver.plugin.mybatis.annotation.Mybatis;
import cn.hserver.plugin.mybatis.proxy.MybatisProxy;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

/**
 * @author hxm
 */
public class MybatisPlugin extends PluginAdapter {

    private static final Logger log = LoggerFactory.getLogger(MybatisPlugin.class);

    private final Set<Class<?>> classes =new HashSet<>();

    @Override
    public PluginInfo getPluginInfo() {
        return new PluginInfo.Builder()
                .name("MyBatis-Plus")
                .description("MyBatis-Plus（简称 MP）是一个 MyBatis 的增强工具，在 MyBatis 的基础上只做增强不做改变，为简化开发、提高效率而生。")
                .build();
    }

    @Override
    public void iocStartScan(Class<?> clazz) {
        Mybatis annotation = clazz.getAnnotation(Mybatis.class);
        if (annotation!=null){
            classes.add(clazz);
        }
    }

    @Override
    public void iocStartPopulate() {
        try {
            Map<String, SqlSessionFactory> stringSqlSessionFactoryMap = MybatisInit.initMybatis(classes);
            if (stringSqlSessionFactoryMap == null) {
                return;
            }
            stringSqlSessionFactoryMap.forEach(IocApplicationContext::addBean);

            for (Class<?> aClass : classes) {
                Mybatis mybatis = aClass.getAnnotation(Mybatis.class);
                String value = mybatis.value();
                if (value.trim().isEmpty()) {
                    value = SqlSessionFactory.class.getName();
                }
                SqlSessionFactory sqlSessionFactory = stringSqlSessionFactoryMap.get(value);
                Object mapper = MybatisProxy.getInstance().getProxy(aClass, sqlSessionFactory);
                IocApplicationContext.addBean(mapper);
            }

        }catch (Exception e){
            log.error(e.getMessage(), e);
        }finally {
            classes.clear();
        }
    }

}
