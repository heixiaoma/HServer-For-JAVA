package cn.hserver.plugin.gateway.handler.tcp;

import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.server.util.ReleaseUtil;
import cn.hserver.plugin.gateway.business.Business;
import cn.hserver.plugin.gateway.business.BusinessTcp;
import cn.hserver.plugin.gateway.config.GateWayConfig;
import cn.hserver.plugin.gateway.handler.ReadWriteLimitHandler;
import cn.hserver.plugin.gateway.handler.http4.Http4BackendHandler;
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
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws InterruptedException {
        try {
            final Channel inboundChannel = ctx.channel();
            Bootstrap b = new Bootstrap();
            b.group(GateWayConfig.EVENT_EXECUTORS);
            b.channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new ReadWriteLimitHandler(inboundChannel, ch));
                            ch.pipeline().addLast(new BackendHandler(inboundChannel, businessTcp));
                        }
                    });
            SocketAddress proxyHost = businessTcp.getProxyHost(ctx, null, ctx.channel().remoteAddress());
            final AtomicInteger count = new AtomicInteger(0);
            //重连等待
            while (true) {
                try {
                    if (outboundChannel != null && outboundChannel.isActive()) {
                        return;
                    }
                    outboundChannel = b.connect(proxyHost).sync().channel();
                } catch (Exception e) {
                    if (!businessTcp.connectController(ctx, false, count.incrementAndGet(), e)) {
                        closeOnFlush(ctx.channel());
                        return;
                    }
                }
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            ctx.close();
        }
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        try {
            Object in = businessTcp.in(ctx, msg);
            if (in == null) {
                ReleaseUtil.release(in);
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
            businessTcp.close(ctx.channel());
            closeOnFlush(outboundChannel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        businessTcp.exceptionCaught(ctx, cause);
        closeOnFlush(ctx.channel());
    }
}
