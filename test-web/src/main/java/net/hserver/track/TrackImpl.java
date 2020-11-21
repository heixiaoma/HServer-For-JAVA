package net.hserver.track;

import javassist.CtMethod;
import top.hserver.core.interfaces.TrackAdapter;
import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.server.util.JvmStack;

/**
 * @author hxm
 */
@Bean
public class TrackImpl implements TrackAdapter {
    @Override
    public void track(Class clazz, CtMethod method, StackTraceElement[] stackTraceElements, long start, long end) throws Exception {
        System.out.println("当前类：{},当前方法：{},耗时：{}" + clazz.getName() + stackTraceElements[1].getMethodName() + (end - start) + "ms");
        JvmStack.printMemoryInfo();
        JvmStack.printGCInfo();
    }
}
