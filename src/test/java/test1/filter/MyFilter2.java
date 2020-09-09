package test1.filter;

import top.hserver.core.interfaces.FilterAdapter;
import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.server.context.Webkit;
import lombok.extern.slf4j.Slf4j;

/**
 * 优先级顺序
 */
@Slf4j
//@Bean
public class MyFilter2 implements FilterAdapter  {

    @Override
    public void doFilter(Webkit webkit) throws Exception {
        log.info("MyFilter->2");
    }
}
