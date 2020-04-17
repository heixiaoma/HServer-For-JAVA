package top.test.track;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import top.hserver.core.interfaces.TrackAdapter;
import top.hserver.core.ioc.annotation.Bean;

import java.lang.reflect.Method;

/**
 * @author hxm
 */
@Bean
@Slf4j
public class TrackImpl implements TrackAdapter {

    @Override
    public void track(Class clazz, StackTraceElement stackTraceElement, long start, long end) throws Exception {
        log.info("当前类：{},当前方法：{},耗时：{}", clazz.getName(), stackTraceElement.getMethodName(), (end - start) + "ms");
    }

}
