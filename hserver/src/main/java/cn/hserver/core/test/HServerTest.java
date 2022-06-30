package cn.hserver.core.test;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import cn.hserver.HServerApplication;
import cn.hserver.core.ioc.IocUtil;

/**
 * @author hxm
 */
public class HServerTest extends BlockJUnit4ClassRunner {
    public HServerTest(Class<?> klass) throws InitializationError {
        super(klass);
        String name = klass.getPackage().getName();
        int i = name.indexOf(".");
        if (i > -1) {
            name = name.substring(0, i);
        }
        HServerApplication.runTest(name,klass);
    }

    @Override
    protected Object createTest() throws Exception {
        return IocUtil.getBean(this.getTestClass().getJavaClass());
    }

}
