package cn.hserver.core.server.util;

import cn.hserver.core.server.context.ConstConfig;
import cn.hserver.core.server.context.IoMultiplexer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.*;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.incubator.channel.uring.IOUring;
import io.netty.incubator.channel.uring.IOUringEventLoopGroup;
import io.netty.incubator.channel.uring.IOUringServerSocketChannel;
import io.netty.incubator.channel.uring.IOUringSocketChannel;
import org.checkerframework.checker.units.qual.C;

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

    public static Class<? extends ServerSocketChannel> getEventLoopTypeClass() {
        if (IOUring.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.IO_URING||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
            return IOUringServerSocketChannel.class;
        } else if (Epoll.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.EPOLL||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
            return EpollServerSocketChannel.class;
        } else if (KQueue.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.KQUEUE||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
            return KQueueServerSocketChannel.class;
        } else {
            return NioServerSocketChannel.class;
        }
    }


    public static Class<? extends SocketChannel> getEventLoopTypeClassClient() {
        if (IOUring.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.IO_URING||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
            return IOUringSocketChannel.class;
        } else if (Epoll.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.EPOLL||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
            return EpollSocketChannel.class;
        } else if (KQueue.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.KQUEUE||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
            return KQueueSocketChannel.class;
        } else {
            return NioSocketChannel.class;
        }
    }


    public static EventLoopGroup getEventLoop(int size, String name) {
        if (IOUring.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.IO_URING||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
            return new IOUringEventLoopGroup(size, new NamedThreadFactory(name));
        } else if (Epoll.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.EPOLL||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
            return new EpollEventLoopGroup(size, new NamedThreadFactory(name));
        } else if (KQueue.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.KQUEUE||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
            return new KQueueEventLoopGroup(size, new NamedThreadFactory(name));
        } else {
            return new NioEventLoopGroup(size, new NamedThreadFactory(name));
        }
    }

}
