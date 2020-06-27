package top.hserver.core.test;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import top.hserver.HServerApplication;
import top.hserver.core.ioc.IocUtil;
import top.hserver.core.ioc.annotation.HServerBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author hxm
 */
public class HServerTest extends BlockJUnit4ClassRunner {
    public HServerTest(Class<?> klass) throws InitializationError {
        super(klass);
        List<Class> list = new ArrayList<>();
        HServerBootTest hServerBootTest = klass.getAnnotation(HServerBootTest.class);
        if (hServerBootTest != null) {
            Class[] value = hServerBootTest.value();
            list.addAll(Arrays.asList(value));
        }
        list.add(klass);
        HServerApplication.runTest(list);
    }

    @Override
    protected Object createTest() throws Exception {
        return IocUtil.getBean(this.getTestClass().getJavaClass());
    }

}
