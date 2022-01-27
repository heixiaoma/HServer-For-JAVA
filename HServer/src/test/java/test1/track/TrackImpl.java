package test1.track;

import javassist.CtMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hserver.core.interfaces.TrackAdapter;
import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.server.util.JvmStack;

/**
 * @author hxm
 */
@Bean
public class TrackImpl implements TrackAdapter {

    private static final Logger log = LoggerFactory.getLogger(TrackImpl.class);

    @Override
    public void track(Class clazz, CtMethod method, StackTraceElement[] stackTraceElements, long start, long end,int pSpanId,int spanId) throws Exception {
        log.info("当前类：{},当前方法：{},耗时：{}", clazz.getName(), stackTraceElements[1].getMethodName(), (end - start) + "ms");
        JvmStack.printMemoryInfo();
        JvmStack.printGCInfo();
    }
}
