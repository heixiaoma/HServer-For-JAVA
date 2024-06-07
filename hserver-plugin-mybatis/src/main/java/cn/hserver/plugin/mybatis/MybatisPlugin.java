package cn.hserver.plugin.mybatis;

import cn.hserver.core.ioc.ref.PackageScanner;
import cn.hserver.plugin.mybatis.annotation.Mybatis;
import cn.hserver.plugin.mybatis.proxy.MybatisProxy;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.hserver.core.interfaces.PluginAdapter;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.annotation.Autowired;
import cn.hserver.core.server.util.ExceptionUtil;
import cn.hserver.plugin.mybatis.bean.MybatisConfig;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 参考文献 https://blog.csdn.net/qq_42413011/article/details/118640420
 *
 * @author hxm
 */
public class MybatisPlugin implements PluginAdapter {

    private static final Logger log = LoggerFactory.getLogger(MybatisPlugin.class);


    @Override
    public void startApp() {

    }

    @Override
    public void startIocInit() {

    }

    @Override
    public Set<Class<?>> iocInitBeanList() {
        return null;
    }

    @Override
    public void iocInit(PackageScanner packageScanner) {
        try {
            Set<Class<?>> annotationList = packageScanner.getAnnotationList(Mybatis.class);
            Map<String, SqlSessionFactory> stringSqlSessionFactoryMap = MybatisInit.initMybatis(annotationList);
            if (stringSqlSessionFactoryMap == null) {
                return;
            }
            stringSqlSessionFactoryMap.forEach(IocUtil::addBean);
            for (Class<?> aClass : annotationList) {
                Mybatis mybatis = aClass.getAnnotation(Mybatis.class);
                String value = mybatis.value();
                if (value.trim().length() == 0) {
                    value = SqlSessionFactory.class.getName();
                }
                SqlSessionFactory sqlSessionFactory = stringSqlSessionFactoryMap.get(value);
                Object mapper = MybatisProxy.getInstance().getProxy(aClass, sqlSessionFactory);
                IocUtil.addBean(mapper);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void iocInitEnd() {

    }

    @Override
    public void startInjection() {

    }
    @Override
    public void injectionEnd() {
        log.info("MybatisPlus插件执行完成");
    }
}
