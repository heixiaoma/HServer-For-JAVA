package net.hserver.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import top.hserver.core.interfaces.ProtocolDispatcherAdapter;
import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.ioc.annotation.Order;
import top.hserver.core.server.ServerInitializer;
import top.hserver.core.server.dispatcher.DispatchHttp;

/**
 * @author hxm
 */
@Order(3)
@Bean
public class HttpProtocol extends DispatchHttp {

    @Override
    public boolean dispatcher(ChannelHandlerContext ctx, ChannelPipeline pipeline, byte[] headers) {
        return super.dispatcher(ctx, pipeline, headers);
    }
}
