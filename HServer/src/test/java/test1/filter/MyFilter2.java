package test1.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hserver.core.interfaces.FilterAdapter;
import top.hserver.core.server.context.Webkit;

/**
 * 优先级顺序
 */
//@Bean
public class MyFilter2 implements FilterAdapter  {
    private static final Logger log = LoggerFactory.getLogger(MyFilter2.class);
    @Override
    public void doFilter(Webkit webkit) throws Exception {
        log.info("MyFilter->2");
    }
}
