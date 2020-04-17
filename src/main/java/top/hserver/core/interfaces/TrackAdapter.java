package top.hserver.core.interfaces;


import java.lang.reflect.Method;

public interface TrackAdapter {
  void track(Class clazz, Method method, long start, long end) throws Exception;
}
