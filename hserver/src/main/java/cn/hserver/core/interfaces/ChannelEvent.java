package cn.hserver.core.interfaces;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author hxm
 */
public interface ChannelEvent {

    void channelActive(ChannelHandlerContext channelHandlerContext);

    void channelRead(ChannelHandlerContext channelHandlerContext, Object msg);

}
