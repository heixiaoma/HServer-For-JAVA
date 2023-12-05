package cn.hserver.plugin.gateway.handler.http7;

import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.server.util.ReleaseUtil;
import cn.hserver.plugin.gateway.business.Business;
import cn.hserver.plugin.gateway.business.BusinessHttp7;
import cn.hserver.plugin.gateway.business.BusinessTcp;
import cn.hserver.plugin.gateway.config.GateWayConfig;
import cn.hserver.plugin.gateway.ssl.HttpsMapperSslContextFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLEngine;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicInteger;


public class Http7FrontendHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(Http7FrontendHandler.class);

    private Channel outboundChannel;

    private BusinessHttp7 businessHttp7;

    public BusinessHttp7 getBusinessHttp7() {
        return businessHttp7;
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        log.debug("限制操作，让两个通道实现同步读写 开关状态:{}",ctx.channel().isWritable());
        outboundChannel.config().setAutoRead(ctx.channel().isWritable());
        super.channelWritabilityChanged(ctx);
    }

    public Http7FrontendHandler() {
        for (Business business : IocUtil.getListBean(Business.class)) {
            if (business instanceof BusinessHttp7) {
                businessHttp7 = (BusinessHttp7) business;
            }
        }
    }

    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final Channel inboundChannel = ctx.channel();
        Bootstrap b = new Bootstrap();
        b.group(GateWayConfig.EVENT_EXECUTORS);
        InetSocketAddress proxyHost = (InetSocketAddress) businessHttp7.getProxyHost(ctx, null, ctx.channel().localAddress());
        b.channel(NioSocketChannel.class).handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) {
                if (proxyHost.getPort() == 443) {
                    SSLEngine sslEngine = HttpsMapperSslContextFactory.getClientContext().createSSLEngine();
                    sslEngine.setUseClientMode(true);
                    ch.pipeline().addFirst(new SslHandler(sslEngine));
                }
                ch.pipeline().addLast(new HttpClientCodec(), new Http7ObjectAggregator(Integer.MAX_VALUE, businessHttp7.ignoreUrls()));
                ch.pipeline().addLast(new Http7BackendHandler(inboundChannel, businessHttp7));
            }
        });

        final AtomicInteger count = new AtomicInteger(0);
        //重连等待
        while (true) {
            try {
                if (outboundChannel != null && outboundChannel.isActive()) {
                    return;
                }
                outboundChannel = b.connect(proxyHost).sync().channel();
            } catch (Exception e) {
                if (!businessHttp7.connectController(ctx, false, count.incrementAndGet(), e)) {
                    closeOnFlush(ctx.channel());
                    return;
                }
            }
        }
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        //数据入场
        try {
            Object in = businessHttp7.in(ctx, msg);
            if (in == null) {
                ReleaseUtil.release(msg);
                return;
            }
            if (outboundChannel == null || !outboundChannel.isActive()) {
                ReleaseUtil.release(msg);
                closeOnFlush(ctx.channel());
                return;
            }
            outboundChannel.writeAndFlush(in).addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    ReleaseUtil.release(in);
                    future.channel().close();
                }
            });
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            ReleaseUtil.release(msg);
            throw e;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (outboundChannel != null) {
            closeOnFlush(outboundChannel);
            businessHttp7.close(ctx.channel());
        }else {
            ctx.fireChannelInactive();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        businessHttp7.exceptionCaught(ctx,cause);
        closeOnFlush(ctx.channel());
    }
}
