package cn.hserver.netty.web.util;

import cn.hserver.core.util.NamedThreadFactory;
import cn.hserver.netty.web.constants.IoMultiplexer;
import cn.hserver.netty.web.constants.NettyConfig;
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
        if (IoUring.isAvailable()&&(NettyConfig.IO_MODE== IoMultiplexer.IO_URING||NettyConfig.IO_MODE==IoMultiplexer.DEFAULT)) {
            return IoMultiplexer.IO_URING;
        } else if (Epoll.isAvailable()&&(NettyConfig.IO_MODE== IoMultiplexer.EPOLL||NettyConfig.IO_MODE==IoMultiplexer.DEFAULT)) {
            return IoMultiplexer.EPOLL;
        } else if (KQueue.isAvailable()&&(NettyConfig.IO_MODE== IoMultiplexer.KQUEUE||NettyConfig.IO_MODE==IoMultiplexer.DEFAULT)) {
            return IoMultiplexer.KQUEUE;
        } else {
            return IoMultiplexer.NIO;
        }
    }

    public static Class<? extends ServerSocketChannel> getEventLoopTypeClass() {
        if (IoUring.isAvailable()&&(NettyConfig.IO_MODE== IoMultiplexer.IO_URING||NettyConfig.IO_MODE==IoMultiplexer.DEFAULT)) {
            return IoUringServerSocketChannel.class;
        } else if (Epoll.isAvailable()&&(NettyConfig.IO_MODE== IoMultiplexer.EPOLL||NettyConfig.IO_MODE==IoMultiplexer.DEFAULT)) {
            return EpollServerSocketChannel.class;
        } else if (KQueue.isAvailable()&&(NettyConfig.IO_MODE== IoMultiplexer.KQUEUE||NettyConfig.IO_MODE==IoMultiplexer.DEFAULT)) {
            return KQueueServerSocketChannel.class;
        } else {
            return NioServerSocketChannel.class;
        }
    }


    public static Class<? extends SocketChannel> getEventLoopTypeClassClient() {
        if (IoUring.isAvailable()&&(NettyConfig.IO_MODE== IoMultiplexer.IO_URING||NettyConfig.IO_MODE==IoMultiplexer.DEFAULT)) {
            return IoUringSocketChannel.class;
        } else if (Epoll.isAvailable()&&(NettyConfig.IO_MODE== IoMultiplexer.EPOLL||NettyConfig.IO_MODE==IoMultiplexer.DEFAULT)) {
            return EpollSocketChannel.class;
        } else if (KQueue.isAvailable()&&(NettyConfig.IO_MODE== IoMultiplexer.KQUEUE||NettyConfig.IO_MODE==IoMultiplexer.DEFAULT)) {
            return KQueueSocketChannel.class;
        } else {
            return NioSocketChannel.class;
        }
    }

    public static Class<? extends DatagramChannel> getEventLoopTypeClassUdp() {
        if (IoUring.isAvailable()&&(NettyConfig.IO_MODE== IoMultiplexer.IO_URING||NettyConfig.IO_MODE==IoMultiplexer.DEFAULT)) {
            return IoUringDatagramChannel.class;
        } else if (Epoll.isAvailable()&&(NettyConfig.IO_MODE== IoMultiplexer.EPOLL||NettyConfig.IO_MODE==IoMultiplexer.DEFAULT)) {
            return EpollDatagramChannel.class;
        } else if (KQueue.isAvailable()&&(NettyConfig.IO_MODE== IoMultiplexer.KQUEUE||NettyConfig.IO_MODE==IoMultiplexer.DEFAULT)) {
            return KQueueDatagramChannel.class;
        } else {
            return NioDatagramChannel.class;
        }
    }


    public static EventLoopGroup getEventLoop(int size, String name) {
        if (IoUring.isAvailable()&&(NettyConfig.IO_MODE== IoMultiplexer.IO_URING||NettyConfig.IO_MODE==IoMultiplexer.DEFAULT)) {
            return new MultiThreadIoEventLoopGroup(size,new NamedThreadFactory(name,false), IoUringIoHandler.newFactory());
        } else if (Epoll.isAvailable()&&(NettyConfig.IO_MODE== IoMultiplexer.EPOLL||NettyConfig.IO_MODE==IoMultiplexer.DEFAULT)) {
            return new MultiThreadIoEventLoopGroup(size,new NamedThreadFactory(name,false), EpollIoHandler.newFactory());
        } else if (KQueue.isAvailable()&&(NettyConfig.IO_MODE== IoMultiplexer.KQUEUE||NettyConfig.IO_MODE==IoMultiplexer.DEFAULT)) {
            return new MultiThreadIoEventLoopGroup(size,new NamedThreadFactory(name,false), KQueueIoHandler.newFactory());
        } else {
            return new MultiThreadIoEventLoopGroup(size,new NamedThreadFactory(name,false),NioIoHandler.newFactory());
        }
    }

}
