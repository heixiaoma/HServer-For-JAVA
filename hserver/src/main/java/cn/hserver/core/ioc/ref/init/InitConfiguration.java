package cn.hserver.core.ioc.ref.init;

import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.annotation.Bean;
import cn.hserver.core.ioc.annotation.Configuration;
import cn.hserver.core.ioc.ref.InitIoc;
import cn.hserver.core.ioc.ref.PackageScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

public class InitConfiguration extends Init{

    public InitConfiguration(Set<String> packages) {
        super(packages);
    }
    private static final Logger log = LoggerFactory.getLogger(InitConfiguration.class);

    @Override
    public void init(PackageScanner scan) throws Exception {
        Set<Class<?>> clasps = scan.getAnnotationList(Configuration.class);
        for (Class aClass : clasps) {
            Method[] methods = aClass.getDeclaredMethods();
            Object o = aClass.newInstance();
            for (Field field : aClass.getDeclaredFields()) {
                //配置类只能注入字段和配置属性
                InitIoc.valuezr(field, o);
                //注入类
                InitIoc.zr(field, o);
            }
            for (Method method : methods) {
                Bean bean = method.getAnnotation(Bean.class);
                if (bean != null) {
                    try {
                        if (method.getParameterTypes().length > 0) {
                            log.warn("类：{} 方法：{}，方法不能有入参", aClass.getName(), method.getName());
                            continue;
                        }
                        method.setAccessible(true);
                        Object invoke = method.invoke(o);
                        if (invoke != null) {
                            String value = bean.value();
                            if (value.trim().length() > 0) {
                                IocUtil.addBean(value, invoke);
                            } else {
                                IocUtil.addBean(invoke.getClass().getName(), invoke);
                            }
                        } else {
                            log.warn("{},方法返回空值，不进入容器", method.getName());
                        }
                    } catch (Exception e) {
                        log.warn("类：{} 方法：{}，执行异常，", aClass.getName(), method.getName());
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
