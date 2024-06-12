package cn.hserver.plugin.gateway.handler.tcp;

import cn.hserver.plugin.gateway.business.Business;
import cn.hserver.plugin.gateway.handler.InBaseChannelInboundHandlerAdapter;

import cn.hserver.plugin.gateway.handler.ReadWriteLimitHandler;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class FrontendHandler extends InBaseChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(FrontendHandler.class);

    public FrontendHandler(Business business) {
        super(business);
    }

    @Override
    public ChannelInitializer<Channel> getChannelInitializer(Channel inboundChannel, InetSocketAddress proxyHost) {
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(new ReadWriteLimitHandler(inboundChannel, ch));
                ch.pipeline().addLast(new BackendHandler(inboundChannel, business));
            }
        };
    }
}
