package cn.hserver.core.server.util;

import com.alibaba.ttl.threadpool.TtlExecutors;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.ThreadPerTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventLoopUtil {

    public static EventLoopGroup getEventLoop(int size, String name) {
        if (EpollUtil.check()) {
            return new EpollEventLoopGroup(size, new NamedThreadFactory(name));
        } else {
            return new NioEventLoopGroup(size, new NamedThreadFactory(name));
        }
    }

}
