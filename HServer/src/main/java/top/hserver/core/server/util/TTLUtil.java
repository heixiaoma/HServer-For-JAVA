package top.hserver.core.server.util;

import com.alibaba.ttl.threadpool.TtlExecutors;
import io.netty.channel.DefaultSelectStrategyFactory;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SelectStrategyFactory;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.*;

import java.nio.channels.spi.SelectorProvider;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


public class TTLUtil {
    public static EventLoopGroup getEventLoop(int size, String name) {
        if (EpollUtil.check()) {
            return new EpollEventLoopGroup(size, new NamedThreadFactory(name));
        } else {
            return new NioEventLoopGroup(size, new NamedThreadFactory(name));
        }
    }
}
