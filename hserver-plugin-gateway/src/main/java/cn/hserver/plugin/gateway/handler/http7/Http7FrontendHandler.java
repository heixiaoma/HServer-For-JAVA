package cn.hserver.plugin.gateway.handler.http7;

import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.server.util.EventLoopUtil;
import cn.hserver.core.server.util.ReleaseUtil;
import cn.hserver.plugin.gateway.business.Business;
import cn.hserver.plugin.gateway.business.BusinessHttp7;
import cn.hserver.plugin.gateway.business.BusinessTcp;
import cn.hserver.plugin.gateway.config.GateWayConfig;
import cn.hserver.plugin.gateway.handler.InBaseChannelInboundHandlerAdapter;
import cn.hserver.plugin.gateway.handler.ReadWriteLimitHandler;
import cn.hserver.plugin.gateway.handler.http7.aggregator.Http7ResponseObjectAggregator;
import cn.hserver.plugin.gateway.ssl.HttpsMapperSslContextFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLEngine;
import java.net.InetSocketAddress;


public class Http7FrontendHandler extends InBaseChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(Http7FrontendHandler.class);

    public Http7FrontendHandler(Business business) {
        super(business);
    }


    public String getRequestIgnoreUrls(){
        BusinessHttp7 business1 = (BusinessHttp7) business;
        return business1.requestIgnoreUrls();
    }


    @Override
    public ChannelInitializer<Channel> getChannelInitializer(Channel inboundChannel,InetSocketAddress proxyHost) {

        BusinessHttp7 business1 = (BusinessHttp7) business;
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) {
                ch.pipeline().addLast(new ReadWriteLimitHandler(inboundChannel, ch));
                if (proxyHost.getPort() == 443) {
                    SSLEngine sslEngine = HttpsMapperSslContextFactory.getClientContext().createSSLEngine();
                    sslEngine.setUseClientMode(true);
                    ch.pipeline().addFirst(new SslHandler(sslEngine));
                }
                ch.pipeline().addLast(new HttpClientCodec(), new Http7ResponseObjectAggregator(Integer.MAX_VALUE, inboundChannel, business1.responseIgnoreUrls()));
                ch.pipeline().addLast(new Http7BackendHandler(inboundChannel, business1));
            }
        };
    }
}
