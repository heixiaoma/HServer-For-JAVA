package cn.hserver.plugin.gateway.handler.http7;

import cn.hserver.core.ioc.IocUtil;
import cn.hserver.plugin.gateway.business.Business;
import cn.hserver.plugin.gateway.business.BusinessHttp7;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicInteger;


public class Http7WebSocketFrontendHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(Http7WebSocketFrontendHandler.class);

    private Channel outboundChannel;

    private static BusinessHttp7 businessHttp7;

    private WebSocketServerHandshaker handshake;

    public Http7WebSocketFrontendHandler() {
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
        if (msg instanceof HttpRequest || msg instanceof WebSocketFrame) {
            outboundChannel.writeAndFlush(msg);
        } else {
            closeOnFlush(ctx.channel());
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        log.debug("限制操作，让两个通道实现同步读写 开关状态:{}",ctx.channel().isWritable());
        ctx.channel().config().setAutoRead(ctx.channel().isWritable());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            handleHttpRequest(ctx, (HttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame msg) {
        if (outboundChannel != null) {
            try {
                Object in = businessHttp7.in(ctx, msg);
                if (in == null) {
                    return;
                }
                outboundChannel.writeAndFlush(in);
            } catch (Throwable e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void writeWebSocket(ChannelHandlerContext ctx, HttpRequest request) throws URISyntaxException {
        try {

            if (outboundChannel == null) {
                Bootstrap b = new Bootstrap();
                b.group(ctx.channel().eventLoop());

                SocketAddress proxyHost = businessHttp7.getProxyHost(ctx, request, ctx.channel().localAddress());
                if (!request.headers().contains(HttpHeaderNames.ORIGIN)) {
                    request.headers().add(HttpHeaderNames.ORIGIN, proxyHost.toString() + request.uri());
                }

                WebSocketClientHandshaker webSocketClientHandshaker = WebSocketClientHandshakerFactory.newHandshaker(
                        new URI(request.uri()), WebSocketVersion.V13, null, true, request.headers());
                Http7WebSocketBackendHandler handler = new Http7WebSocketBackendHandler(
                        webSocketClientHandshaker,
                        ctx.channel(),
                        businessHttp7
                );
                b.channel(NioSocketChannel.class).handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(new HttpClientCodec(), new HttpObjectAggregator(Integer.MAX_VALUE), WebSocketClientCompressionHandler.INSTANCE, handler);
                    }
                });
                final AtomicInteger count = new AtomicInteger(0);

                //数据代理服务选择器
                ChannelFuture f = b.connect(proxyHost).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            try {
                                handler.handshakeFuture().addListener((future1) -> {
                                    future1.sync();
                                    future.channel().writeAndFlush(request);
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                ReferenceCountUtil.release(request);
                            }
                            businessHttp7.connectController(ctx, true, count.incrementAndGet(), null);
                        } else {
                            future.channel().close();
                            ReferenceCountUtil.release(request);
                            if (businessHttp7.connectController(ctx, false, count.incrementAndGet(), future.cause())) {
                                b.connect(proxyHost).addListener(this);
                            }
                        }
                    }
                });
                outboundChannel = f.channel();
            } else {
                read(ctx, request);
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            ReferenceCountUtil.release(request);
            throw e;
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req) throws Exception {
        if (isWebSocketRequest(req)) {
            Object in = businessHttp7.in(ctx, req);
            if (in == null) {
                return;
            }
            req = (HttpRequest) in;
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(req.uri(), null, true);
            this.handshake = wsFactory.newHandshaker(req);
            if (this.handshake == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                this.handshake.handshake(ctx.channel(), req);
                writeWebSocket(ctx, req);
            }
        } else {
            ctx.fireChannelRead(req);
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

    private boolean isWebSocketRequest(Object msg) {
        HttpRequest req = (HttpRequest) msg;
        return req != null
                && req.decoderResult().isSuccess()
                && "websocket".equals(req.headers().get("Upgrade"));
    }
}
