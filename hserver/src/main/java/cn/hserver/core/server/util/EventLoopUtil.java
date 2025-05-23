package cn.hserver.core.server.util;

import cn.hserver.core.server.context.ConstConfig;
import cn.hserver.core.server.context.IoMultiplexer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.epoll.*;
import io.netty.channel.kqueue.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.uring.*;

public class EventLoopUtil {


    public static IoMultiplexer getEventLoopType() {
        if (IoUring.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.IO_URING||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
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
        if (IoUring.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.IO_URING||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
            return IoUringServerSocketChannel.class;
        } else if (Epoll.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.EPOLL||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
            return EpollServerSocketChannel.class;
        } else if (KQueue.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.KQUEUE||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
            return KQueueServerSocketChannel.class;
        } else {
            return NioServerSocketChannel.class;
        }
    }


    public static Class<? extends SocketChannel> getEventLoopTypeClassClient() {
        if (IoUring.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.IO_URING||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
            return IoUringSocketChannel.class;
        } else if (Epoll.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.EPOLL||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
            return EpollSocketChannel.class;
        } else if (KQueue.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.KQUEUE||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
            return KQueueSocketChannel.class;
        } else {
            return NioSocketChannel.class;
        }
    }

    public static Class<? extends DatagramChannel> getEventLoopTypeClassUdp() {
        if (IoUring.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.IO_URING||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
            return IoUringDatagramChannel.class;
        } else if (Epoll.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.EPOLL||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
            return EpollDatagramChannel.class;
        } else if (KQueue.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.KQUEUE||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
            return KQueueDatagramChannel.class;
        } else {
            return NioDatagramChannel.class;
        }
    }


    public static EventLoopGroup getEventLoop(int size, String name) {
        if (IoUring.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.IO_URING||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
            return new MultiThreadIoEventLoopGroup(size,new NamedThreadFactory(name), IoUringIoHandler.newFactory());
        } else if (Epoll.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.EPOLL||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
            return new MultiThreadIoEventLoopGroup(size,new NamedThreadFactory(name), EpollIoHandler.newFactory());
        } else if (KQueue.isAvailable()&&(ConstConfig.IO_MOD== IoMultiplexer.KQUEUE||ConstConfig.IO_MOD==IoMultiplexer.DEFAULT)) {
            return new MultiThreadIoEventLoopGroup(size,new NamedThreadFactory(name), KQueueIoHandler.newFactory());
        } else {
            return new MultiThreadIoEventLoopGroup(size,new NamedThreadFactory(name),NioIoHandler.newFactory());
        }
    }

}
