package cn.hserver.core.ioc.ref.init;

import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.ref.PackageScanner;

import java.lang.annotation.Annotation;
import java.util.Set;

public class InitTest extends Init{

    public InitTest(Set<String> packages) {
        super(packages);
    }

    @Override
    public void init(PackageScanner scanner) {
        try {
            Class<Annotation> aClass1 = (Class<Annotation>) InitBean.class.getClassLoader().loadClass("org.junit.runner.RunWith");
            Set<Class<?>> clasps = scanner.getAnnotationList(aClass1);
            for (Class aClass : clasps) {
                //检查注解里面是否有值
                IocUtil.addBean(aClass.getName(), aClass.newInstance());
            }
        } catch (Exception ignored) {
        }
    }
}
