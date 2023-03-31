package cn.hserver.plugin.gateway.handler.http4;

import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.server.context.ConstConfig;
import cn.hserver.core.server.util.EventLoopUtil;
import cn.hserver.core.server.util.ReleaseUtil;
import cn.hserver.plugin.gateway.bean.Http4Data;
import cn.hserver.plugin.gateway.business.Business;
import cn.hserver.plugin.gateway.business.BusinessHttp4;
import cn.hserver.plugin.gateway.business.BusinessHttp7;
import cn.hserver.plugin.gateway.config.GateWayConfig;
import cn.hserver.plugin.gateway.handler.tcp.BackendHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

public class Http4FrontendHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(Http4FrontendHandler.class);

    private Channel outboundChannel;
    private final String host;
    private static BusinessHttp4 businessHttp4;

    public Http4FrontendHandler(String host) {
        this.host = host;
        for (Business business : IocUtil.getListBean(Business.class)) {
            if (business instanceof BusinessHttp4) {
                businessHttp4 = (BusinessHttp4) business;
            }
        }
    }

    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        if (outboundChannel != null) {
            outboundChannel.config().setAutoRead(ctx.channel().isWritable());
        }
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final Channel inboundChannel = ctx.channel();
        Bootstrap b = new Bootstrap();
        b.group(GateWayConfig.EVENT_EXECUTORS)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new Http4BackendHandler(inboundChannel, businessHttp4));
                    }
                });
        SocketAddress proxyHost = businessHttp4.getProxyHost(ctx, new Http4Data(host, null), ctx.channel().remoteAddress());
        final AtomicInteger count = new AtomicInteger(0);
        ChannelFuture f = b.connect(proxyHost).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    businessHttp4.connectController(ctx,true,count.incrementAndGet(),null);
                } else {
                    future.channel().close();
                    if (businessHttp4.connectController(ctx,false,count.incrementAndGet(),future.cause())){
                        b.connect(proxyHost).addListener(this);
                    }
                }
            }
        });
        outboundChannel = f.channel();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {

        try {
            Object in = businessHttp4.in(ctx, new Http4Data(host, msg));
            if (in == null) {
                ReleaseUtil.release(msg);
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
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (outboundChannel != null) {
            closeOnFlush(outboundChannel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!(cause instanceof IOException)) {
            log.error("WEB通道 ......", cause);
        }
        closeOnFlush(ctx.channel());
    }
}
