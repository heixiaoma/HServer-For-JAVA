package cn.hserver.plugin.gateway.handler.http7;

import cn.hserver.core.ioc.IocUtil;
import cn.hserver.plugin.gateway.business.Business;
import cn.hserver.plugin.gateway.business.BusinessHttp7;
import cn.hserver.plugin.gateway.business.BusinessTcp;
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


public class Http7FrontendHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(Http7FrontendHandler.class);

    private Channel outboundChannel;

    private static BusinessHttp7 businessHttp7;

    public Http7FrontendHandler() {
        for (Business business : IocUtil.getListBean(Business.class)) {
            if (business instanceof BusinessHttp7) {
                businessHttp7 = (BusinessHttp7) business;
            }
        }
    }

    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            businessHttp7.close(ch);
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void read(final ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            outboundChannel.writeAndFlush(msg);
        } else {
            closeOnFlush(ctx.channel());
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        //数据入场
        try {
            Object in = businessHttp7.in(ctx, msg);
            if (in == null) {
                return;
            }


            if (outboundChannel == null) {
                final Channel inboundChannel = ctx.channel();
                Bootstrap b = new Bootstrap();
                b.group(ctx.channel().eventLoop());
                InetSocketAddress proxyHost = (InetSocketAddress) businessHttp7.getProxyHost(ctx, in, ctx.channel().localAddress());
                SSLEngine sslEngine = HttpsMapperSslContextFactory.getClientContext().createSSLEngine();
                sslEngine.setUseClientMode(true);
                b.channel(NioSocketChannel.class).handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        if (proxyHost.getPort() == 443) {
                            ch.pipeline().addFirst(new SslHandler(sslEngine));
                        }
                        ch.pipeline().addLast(new HttpClientCodec(), new HttpContentDecompressor(), new HttpObjectAggregator(Integer.MAX_VALUE));
                        ch.pipeline().addLast(new Http7BackendHandler(inboundChannel, businessHttp7));
                    }
                });
                //数据代理服务选择器
                ChannelFuture f = b.connect(proxyHost).addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        future.channel().writeAndFlush(in);
                    } else {
                        future.channel().close();
                        ReferenceCountUtil.release(in);
                    }
                });
                outboundChannel = f.channel();
            } else {
                read(ctx, in);
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            ctx.close();
            ReferenceCountUtil.release(msg);
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
        closeOnFlush(ctx.channel());
    }
}
