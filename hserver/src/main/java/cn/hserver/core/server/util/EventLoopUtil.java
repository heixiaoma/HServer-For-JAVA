package cn.hserver.core.server.util;

import cn.hserver.core.server.context.ConstConfig;
import cn.hserver.core.server.context.IoMultiplexer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.incubator.channel.uring.IOUring;
import io.netty.channel.epoll.Epoll;
import io.netty.incubator.channel.uring.IOUringEventLoopGroup;

public class EventLoopUtil {


    public static IoMultiplexer getEventLoopType() {
        if (IOUring.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.IO_URING||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
            return IoMultiplexer.IO_URING;
        } else if (Epoll.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.EPOLL||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
            return IoMultiplexer.EPOLL;
        } else if (KQueue.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.KQUEUE||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
            return IoMultiplexer.KQUEUE;
        } else {
            return IoMultiplexer.JDK;
        }
    }


    public static EventLoopGroup getEventLoop(int size, String name) {
        if (IOUring.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.IO_URING||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
            return new IOUringEventLoopGroup(size, new NamedThreadFactory(name));
        } else if (Epoll.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.EPOLL||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
            return new EpollEventLoopGroup(size, new NamedThreadFactory(name));
        } else if (KQueue.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.KQUEUE||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
            return new EpollEventLoopGroup(size, new NamedThreadFactory(name));
        } else {
            return new NioEventLoopGroup(size, new NamedThreadFactory(name));
        }
    }

}
