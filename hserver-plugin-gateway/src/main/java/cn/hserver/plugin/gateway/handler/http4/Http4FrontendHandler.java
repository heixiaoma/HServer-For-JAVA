package cn.hserver.plugin.gateway.handler.http4;

import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.server.context.ConstConfig;
import cn.hserver.core.server.util.EventLoopUtil;
import cn.hserver.core.server.util.ReleaseUtil;
import cn.hserver.plugin.gateway.bean.Http4Data;
import cn.hserver.plugin.gateway.business.Business;
import cn.hserver.plugin.gateway.handler.InBaseChannelInboundHandlerAdapter;
import cn.hserver.plugin.gateway.handler.ReadWriteLimitHandler;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class Http4FrontendHandler extends InBaseChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(Http4FrontendHandler.class);

    private final String host;

    public Http4FrontendHandler(String host, Business business) {
        super(business);
        this.host = host;
    }

    @Override
    public ChannelInitializer<Channel> getChannelInitializer(Channel inboundChannel, InetSocketAddress proxyHost) {
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(new ReadWriteLimitHandler(inboundChannel, ch));
                ch.pipeline().addLast(new Http4BackendHandler(inboundChannel, business));
            }
        };
    }

    @Override
    public Object getHost() {
        return new Http4Data(host, null);
    }

    @Override
    public Object getMessage(Object msg) {
        return new Http4Data(host, msg);
    }
}
