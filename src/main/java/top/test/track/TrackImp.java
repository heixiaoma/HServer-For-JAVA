package top.test.track;

import top.hserver.core.interfaces.TrackAdapter;
import top.hserver.core.ioc.annotation.Bean;

import java.lang.reflect.Method;

@Bean
public class TrackImp implements TrackAdapter {

  @Override
  public void track(Class clazz, Method method, long start, long end) throws Exception {

  }

}
