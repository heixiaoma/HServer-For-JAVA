package top.hserver.core.interfaces;

import javassist.CtMethod;

/**
 * @author hxm
 */
public interface TrackAdapter {

    /**
     * 当前被调用的的方法信息
     * @param clazz
     * @param method
     * @param stackTraceElements
     * @param start
     * @param end
     * @throws Exception
     */
    void track(Class clazz, CtMethod method, StackTraceElement[] stackTraceElements, long start, long end,long pSpanId,long spanId) throws Exception;

}
