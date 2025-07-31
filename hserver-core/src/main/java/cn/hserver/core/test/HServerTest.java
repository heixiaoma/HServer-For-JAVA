package cn.hserver.core.test;

import cn.hserver.core.boot.HServerApplication;
import cn.hserver.core.context.IocApplicationContext;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

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
        HServerApplication.runTest(klass,name);
    }

    @Override
    protected Object createTest() throws Exception {
        return IocApplicationContext.getBean(this.getTestClass().getJavaClass());
    }

}
