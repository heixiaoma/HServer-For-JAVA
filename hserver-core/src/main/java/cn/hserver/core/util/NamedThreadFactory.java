package cn.hserver.core.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author hxm
 */
public class NamedThreadFactory implements ThreadFactory {

    private final String prefix;
    private final LongAdder threadNumber = new LongAdder();

    public NamedThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        threadNumber.add(1);
        Thread thread = new Thread(runnable, prefix + "@" + threadNumber.intValue());
        //不要卡线程
        thread.setDaemon(true);
        return thread;
    }
}