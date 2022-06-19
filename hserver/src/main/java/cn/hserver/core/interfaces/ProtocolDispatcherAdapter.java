package cn.hserver.core.interfaces;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;

/**
 * @author hxm
 */
public interface ProtocolDispatcherAdapter {
    boolean dispatcher(ChannelHandlerContext ctx, ChannelPipeline pipeline, byte[] headers);
}
