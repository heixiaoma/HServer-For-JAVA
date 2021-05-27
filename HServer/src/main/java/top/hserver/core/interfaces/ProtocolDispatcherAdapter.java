package top.hserver.core.interfaces;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import top.hserver.core.server.ServerInitializer;

/**
 * @author hxm
 */
public interface ProtocolDispatcherAdapter {
    boolean dispatcher(ChannelHandlerContext ctx, ChannelPipeline pipeline, byte[] headers, ServerInitializer.ProtocolDispatcher protocolDispatcher);
}
