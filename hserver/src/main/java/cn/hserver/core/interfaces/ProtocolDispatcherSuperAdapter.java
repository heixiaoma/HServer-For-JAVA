package cn.hserver.core.interfaces;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;

/**
 * @author hxm
 * 用于多端口连接后才返回数据才分发
 */
public interface ProtocolDispatcherSuperAdapter {
    boolean dispatcher(Channel channel, ChannelPipeline pipeline);
}
