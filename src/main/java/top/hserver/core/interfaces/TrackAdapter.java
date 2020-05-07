package top.hserver.core.interfaces;

import java.lang.reflect.Method;

/**
 * @author hxm
 */
public interface TrackAdapter {

    /**
     * 当前被调用的的方法信息
     *
     * @param clazz
     * @param stackTraceElements
     * @param start
     * @param end
     * @throws Exception
     */
    void track(Class clazz,Method method, StackTraceElement[] stackTraceElements, long start, long end) throws Exception;

}
