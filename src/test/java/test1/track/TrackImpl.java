package test1.track;

import lombok.extern.slf4j.Slf4j;
import top.hserver.core.interfaces.TrackAdapter;
import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.server.util.JvmStack;

import java.lang.reflect.Method;

/**
 * @author hxm
 */
@Bean
@Slf4j
public class TrackImpl implements TrackAdapter {
    @Override
    public void track(Class clazz, Method method, StackTraceElement[] stackTraceElements, long start, long end) throws Exception {
        log.info("当前类：{},当前方法：{},耗时：{}", clazz.getName(), stackTraceElements[1].getMethodName(), (end - start) + "ms");
        JvmStack.printMemoryInfo();
        JvmStack.printGCInfo();
    }
}
