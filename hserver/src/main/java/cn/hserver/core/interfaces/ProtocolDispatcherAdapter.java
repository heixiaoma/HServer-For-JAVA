package cn.hserver.core.interfaces;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;

/**
 * @author hxm
 * 用于单独端口多协议，发送数据后才开始分发
 */
public interface ProtocolDispatcherAdapter {
    boolean dispatcher(ChannelHandlerContext ctx, ChannelPipeline pipeline, byte[] headers);
}
