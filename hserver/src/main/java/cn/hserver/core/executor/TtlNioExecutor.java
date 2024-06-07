package cn.hserver.core.executor;

import com.alibaba.ttl.threadpool.TtlExecutors;
import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class TtlNioExecutor extends NioEventLoopGroup {

    public TtlNioExecutor(int nThreads, ThreadFactory threadFactory) {
        super(nThreads, threadFactory);
    }

    @Override
    protected EventLoop newChild(Executor executor, Object... args) throws Exception {
        Executor ttlExecutor = TtlExecutors.getTtlExecutor(executor);
        return super.newChild(ttlExecutor, args);
    }
}
