package top.hserver.core.server.util;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class TTLUtil {

    public static EventLoopGroup getEventLoop(int size, String name) {
        if (EpollUtil.check()) {
            return new EpollEventLoopGroup(size, new NamedThreadFactory(name));
        } else {
            return new NioEventLoopGroup(size, new NamedThreadFactory(name));
        }
    }
}
