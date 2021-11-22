package top.hserver.core.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hserver.core.interfaces.ServerCloseAdapter;
import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.queue.fmap.MemoryData;

@Bean
public class QueueClose implements ServerCloseAdapter {

    private static final Logger log = LoggerFactory.getLogger(QueueClose.class);

    @Override
    public void close() {
        if (MemoryData.size()>0){
            MemoryData.sync();
            log.debug("内存中的队列数据已经全部缓存本地，共：{} 条",MemoryData.size());
        }
    }
}
