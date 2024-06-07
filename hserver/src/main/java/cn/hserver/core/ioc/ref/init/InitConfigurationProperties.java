package cn.hserver.core.ioc.ref.init;

import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.annotation.ConfigurationProperties;
import cn.hserver.core.ioc.ref.PackageScanner;
import cn.hserver.core.server.util.ObjConvertUtil;
import cn.hserver.core.server.util.PropUtil;

import java.lang.reflect.Field;
import java.util.Set;

public class InitConfigurationProperties extends Init{
    public InitConfigurationProperties(Set<String> packages) {
        super(packages);
    }

    @Override
    public void init(PackageScanner scanner) throws Exception {
        //配置文件类自动装配
        Set<Class<?>> clasps = scanner.getAnnotationList(ConfigurationProperties.class);
        for (Class<?> clasp : clasps) {
            String value = clasp.getAnnotation(ConfigurationProperties.class).prefix();
            if (value.trim().length() == 0) {
                value = null;
            }
            Object o = clasp.newInstance();
            for (Field field : clasp.getDeclaredFields()) {
                PropUtil instance = PropUtil.getInstance();
                String s = instance.get(value == null ? field.getName() : value + "." + field.getName(), null);
                Object convert = ObjConvertUtil.convert(field.getType(), s);
                if (convert != null) {
                    field.setAccessible(true);
                    field.set(o, convert);
                }
            }
            IocUtil.addBean(clasp.getName(), o);
        }

    }
}
