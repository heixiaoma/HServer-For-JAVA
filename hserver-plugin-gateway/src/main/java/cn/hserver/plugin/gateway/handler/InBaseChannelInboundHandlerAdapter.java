package cn.hserver.plugin.gateway.handler;

import cn.hserver.core.server.util.EventLoopUtil;
import cn.hserver.core.server.util.ReleaseUtil;
import cn.hserver.plugin.gateway.business.Business;
import cn.hserver.plugin.gateway.config.GateWayConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;


public abstract class InBaseChannelInboundHandlerAdapter extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(InBaseChannelInboundHandlerAdapter.class);

    private volatile Channel outboundChannel;

    protected final Business business;

    public InBaseChannelInboundHandlerAdapter(Business business) {
        this.business = business;
    }

    public abstract ChannelInitializer<Channel> getChannelInitializer(Channel inboundChannel, InetSocketAddress proxyHost);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final Channel inboundChannel = ctx.channel();
        if (outboundChannel != null && outboundChannel.isActive()) {
            return;
        }
        InetSocketAddress proxyHost = (InetSocketAddress) business.getProxyHost(ctx, getHost(), ctx.channel().localAddress());
        Bootstrap b = new Bootstrap();
        b.group(GateWayConfig.EVENT_EXECUTORS);
        b.channel(EventLoopUtil.getEventLoopTypeClassClient()).handler(getChannelInitializer(inboundChannel, proxyHost));
        outboundChannel = b.connect(proxyHost).sync().channel();
        outboundChannel.closeFuture().addListener(future -> {
            inboundChannel.close();
        });
        inboundChannel.closeFuture().addListener(future -> {
            outboundChannel.close();
        });

    }


    public Object getMessage(Object msg) {
        return msg;
    }

    public Object getHost() {
        return null;
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        //数据入场
        try {
            Object in = business.in(ctx, getMessage(msg));
            if (in == null) {
                ReleaseUtil.release(msg);
                return;
            }
            if (outboundChannel == null || !outboundChannel.isActive()) {
                ReleaseUtil.release(msg);
                ctx.channel().close();
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
            outboundChannel.close();
        }
        business.close(ctx.channel());
        ctx.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        business.exceptionCaught(ctx, cause);
    }
}
