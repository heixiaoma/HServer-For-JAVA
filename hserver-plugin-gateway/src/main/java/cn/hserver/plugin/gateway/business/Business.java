package cn.hserver.plugin.gateway.business;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.net.SocketAddress;

public interface Business<T, U> {
    /**
     * 数据入场
     * 业务处理模式，拦截器 限流等
     *
     * @param t
     * @return
     */
    Object in(ChannelHandlerContext ctx, T t);


    /**
     * 代理host
     * 业务代码只处理选择什么样的服务，比如常见随机模式 hash模式 循环模式等
     *
     * @param t
     * @param sourceSocketAddress
     * @return
     */
    SocketAddress getProxyHost(ChannelHandlerContext ctx, T t, SocketAddress sourceSocketAddress);


    /**
     * 数据出场
     * 数据加密等操作
     *
     * @param u
     * @return
     */
    Object out(Channel channel, U u);

    /**
     * 链接关闭
     *
     * @param channel
     */
    void close(Channel channel);

    /**
     * 全局异常
     * @param ctx
     * @param cause
     */
    void exceptionCaught(ChannelHandlerContext ctx, Throwable cause);
}
