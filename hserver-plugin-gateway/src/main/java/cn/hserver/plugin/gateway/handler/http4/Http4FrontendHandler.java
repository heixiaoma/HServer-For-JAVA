package cn.hserver.plugin.gateway.handler.http4;

import cn.hserver.core.ioc.IocUtil;
import cn.hserver.plugin.gateway.bean.Http4Data;
import cn.hserver.plugin.gateway.business.Business;
import cn.hserver.plugin.gateway.business.BusinessHttp4;
import cn.hserver.plugin.gateway.business.BusinessHttp7;
import cn.hserver.plugin.gateway.handler.tcp.BackendHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            businessHttp4.close(ch);
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }


    public void write(ChannelHandlerContext ctx, Object msg) {
        if (outboundChannel!=null&&outboundChannel.isActive()) {
            outboundChannel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    future.channel().close();
                    ReferenceCountUtil.release(msg);
                }
            });
        }
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        if (outboundChannel != null && outboundChannel.isActive()) {
            log.debug("限制操作，让两个通道实现同步读写 开关状态:{}", ctx.channel().isWritable());
            outboundChannel.config().setAutoRead(ctx.channel().isWritable());
        }
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
                    .handler(new Http4BackendHandler(inboundChannel, businessHttp4));
            SocketAddress proxyHost = businessHttp4.getProxyHost(ctx, null, ctx.channel().remoteAddress());
            final AtomicInteger count = new AtomicInteger(0);

            ChannelFuture f = b.connect(proxyHost).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        inboundChannel.read();
                        businessHttp4.connectController(ctx, true, count.incrementAndGet(), null);
                    } else {
                        inboundChannel.close();
                        if (businessHttp4.connectController(ctx, false, count.incrementAndGet(), future.cause())) {
                            b.connect(proxyHost).addListener(this);
                        }
                    }
                }
            });
            outboundChannel = f.channel();
            ctx.channel().config().setAutoRead(false);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            ctx.close();
        }
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {

        try {
            Object in = businessHttp4.in(ctx, new Http4Data(host, msg));
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
        businessHttp4.exceptionCaught(ctx, cause);
        closeOnFlush(ctx.channel());
    }
}
