package cn.hserver.plugin.mybatis.flex;

import cn.hserver.core.interfaces.PluginAdapter;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.ref.PackageScanner;
import cn.hserver.plugin.mybatis.flex.annotation.Mybatis;
import com.mybatisflex.core.MybatisFlexBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

public class MybatisFlexPlugin implements PluginAdapter {

    private static final Logger log = LoggerFactory.getLogger(MybatisFlexPlugin.class);

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
            MybatisFlexBootstrap mybatisFlexBootstrap = MybatisFlexConfig.init(annotationList);
            for (Class<?> aClass : annotationList) {
                Object mapper = mybatisFlexBootstrap.getMapper(aClass);
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
        log.info("MybatisFlex插件执行完成");
    }

}
