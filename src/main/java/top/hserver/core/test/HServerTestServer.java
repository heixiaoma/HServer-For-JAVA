package top.hserver.core.test;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import top.hserver.HServerApplication;
import top.hserver.core.ioc.IocUtil;

/**
 * @author hxm
 */
public class HServerTestServer extends BlockJUnit4ClassRunner {
    public HServerTestServer(Class<?> klass) throws InitializationError {
        super(klass);
        String name = klass.getPackage().getName();
        int i = name.indexOf(".");
        if (i > -1) {
            name = name.substring(0, i);
        }
        HServerApplication.runTest(name,8888,klass);
    }

    @Override
    protected Object createTest() throws Exception {
        return IocUtil.getBean(this.getTestClass().getJavaClass());
    }

}
