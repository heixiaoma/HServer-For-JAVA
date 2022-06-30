package cn.hserver.plugin.rpc.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;
import cn.hserver.plugin.rpc.codec.Msg;
import cn.hserver.plugin.rpc.codec.RpcDecoder;
import cn.hserver.plugin.rpc.codec.RpcEncoder;
import cn.hserver.plugin.rpc.server.ServerHandler;
import cn.hserver.core.interfaces.ProtocolDispatcherAdapter;
import cn.hserver.core.ioc.annotation.Bean;
import cn.hserver.core.ioc.annotation.Order;

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
