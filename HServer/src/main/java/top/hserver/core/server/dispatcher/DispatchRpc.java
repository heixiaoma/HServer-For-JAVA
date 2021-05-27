package top.hserver.core.server.dispatcher;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import top.hserver.cloud.common.Msg;
import top.hserver.cloud.common.codec.RpcDecoder;
import top.hserver.cloud.common.codec.RpcEncoder;
import top.hserver.cloud.server.handler.RpcServerHandler;
import top.hserver.core.interfaces.ProtocolDispatcherAdapter;
import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.ioc.annotation.Order;
import top.hserver.core.server.ServerInitializer;
import top.hserver.core.server.context.ConstConfig;

/**
 * @author hxm
 */
@Order(5)
@Bean
public class DispatchRpc implements ProtocolDispatcherAdapter {

    @Override
    public boolean dispatcher(ChannelHandlerContext ctx, ChannelPipeline pipeline, byte[] headers, ServerInitializer.ProtocolDispatcher protocolDispatcher) {
        pipeline.addLast(new RpcDecoder(Msg.class));
        pipeline.addLast(new RpcEncoder(Msg.class));
        pipeline.addLast(ConstConfig.BUSINESS_EVENT, "RpcServerProviderHandler", new RpcServerHandler());
        pipeline.remove(protocolDispatcher);
        ctx.fireChannelActive();
        return true;
    }
}
