package net.hserver.plugin.rpc.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;
import net.hserver.plugin.rpc.codec.Msg;
import net.hserver.plugin.rpc.codec.RpcDecoder;
import net.hserver.plugin.rpc.codec.RpcEncoder;
import net.hserver.plugin.rpc.server.ServerHandler;
import net.hserver.core.interfaces.ProtocolDispatcherAdapter;
import net.hserver.core.ioc.annotation.Bean;
import net.hserver.core.ioc.annotation.Order;

@Order(5)
@Bean
public class DispatchRpc implements ProtocolDispatcherAdapter {

    public boolean dispatcher(ChannelHandlerContext ctx, ChannelPipeline pipeline, byte[] headers) {
        if (headers[3] == 82 && headers[7] == 80 && headers[11] == 67) {
            pipeline.addLast(new RpcDecoder(Msg.class));
            pipeline.addLast(new RpcEncoder(Msg.class));
            pipeline.addLast("idleStateHandler", new IdleStateHandler(10, 5, 10));
            pipeline.addLast("ServerProviderHandler", new ServerHandler());
            return true;
        } else {
            return false;
        }
    }
}
