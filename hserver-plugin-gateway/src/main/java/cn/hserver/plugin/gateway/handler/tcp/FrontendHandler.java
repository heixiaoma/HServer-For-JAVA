package cn.hserver.plugin.gateway.handler.tcp;

import cn.hserver.core.ioc.IocUtil;
import cn.hserver.plugin.gateway.business.Business;
import cn.hserver.plugin.gateway.business.BusinessTcp;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

public class FrontendHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(FrontendHandler.class);

    private Channel outboundChannel;
    private static BusinessTcp businessTcp;

    public FrontendHandler() {
        for (Business business : IocUtil.getListBean(Business.class)) {
            if (business instanceof BusinessTcp) {
                businessTcp = (BusinessTcp) business;
            }
        }
    }

    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            businessTcp.close(ch);
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        log.debug("限制操作，让两个通道实现同步读写 开关状态:{}",ctx.channel().isWritable());
        outboundChannel.config().setAutoRead(ctx.channel().isWritable());
        super.channelWritabilityChanged(ctx);
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws InterruptedException {
        try {
            final Channel inboundChannel = ctx.channel();
            Bootstrap b = new Bootstrap();
            b.group(inboundChannel.eventLoop());
            b.option(ChannelOption.AUTO_READ, true)
                    .channel(NioSocketChannel.class)
                    .handler(new BackendHandler(inboundChannel, businessTcp));
            SocketAddress proxyHost = businessTcp.getProxyHost(ctx, null, ctx.channel().remoteAddress());
            final AtomicInteger count = new AtomicInteger(0);

            ChannelFuture f = b.connect(proxyHost).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        inboundChannel.read();
                        businessTcp.connectController(ctx, true, count.incrementAndGet(), null);
                    } else {
                        inboundChannel.close();
                        if (businessTcp.connectController(ctx, false, count.incrementAndGet(), future.cause())) {
                            b.connect(proxyHost).addListener(this);
                        }
                    }
                }
            });
            outboundChannel = f.channel();
            ctx.channel().config().setAutoRead(false);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            ctx.close();
        }
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        try {
            Object in = businessTcp.in(ctx, msg);
            if (in == null) {
                return;
            }
            if (outboundChannel.isActive()) {
                outboundChannel.writeAndFlush(in).addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        ctx.channel().read();
                    } else {
                        ReferenceCountUtil.release(in);
                        future.channel().close();
                    }
                });
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            ReferenceCountUtil.release(msg);
            throw e;
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
        businessTcp.exceptionCaught(ctx,cause);
        closeOnFlush(ctx.channel());
    }
}
