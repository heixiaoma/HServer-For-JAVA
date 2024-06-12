package cn.hserver.plugin.gateway.handler.http7;

import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.server.util.EventLoopUtil;
import cn.hserver.plugin.gateway.business.Business;
import cn.hserver.plugin.gateway.business.BusinessHttp7;
import cn.hserver.plugin.gateway.config.GateWayConfig;
import cn.hserver.plugin.gateway.handler.ReadWriteLimitHandler;
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

    private BusinessHttp7 businessHttp7;

    private WebSocketServerHandshaker handshake;


    public Http7WebSocketFrontendHandler(Business businessHttp7) {
        this.businessHttp7= (BusinessHttp7) businessHttp7;
    }

    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void read(final ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest || msg instanceof WebSocketFrame) {
            outboundChannel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    future.channel().close();
                    ReferenceCountUtil.release(msg);
                }
            });
        } else {
            closeOnFlush(ctx.channel());
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            handleHttpRequest(ctx, (HttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        } else {
            ReferenceCountUtil.retain(msg);
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

            if (outboundChannel == null || !outboundChannel.isActive()) {
                Bootstrap b = new Bootstrap();
                b.group(GateWayConfig.EVENT_EXECUTORS);

                SocketAddress proxyHost = businessHttp7.getProxyHost(ctx, request, ctx.channel().localAddress());
                if (!request.headers().contains(HttpHeaderNames.ORIGIN)) {
                    request.headers().add(HttpHeaderNames.ORIGIN, proxyHost.toString() + request.uri());
                }
                String subProtocols = request.headers().get("Sec-WebSocket-Protocol");

                WebSocketClientHandshaker webSocketClientHandshaker = WebSocketClientHandshakerFactory.newHandshaker(
                        new URI(request.uri()), WebSocketVersion.V13, subProtocols, true, request.headers());
                Http7WebSocketBackendHandler handler = new Http7WebSocketBackendHandler(
                        webSocketClientHandshaker,
                        ctx.channel(),
                        businessHttp7
                );
                b.channel(EventLoopUtil.getEventLoopTypeClassClient()).handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addFirst(new ReadWriteLimitHandler(ctx.channel(), ch));
                        ch.pipeline().addLast(new HttpClientCodec(), new HttpObjectAggregator(Integer.MAX_VALUE), WebSocketClientCompressionHandler.INSTANCE, handler);
                    }
                });

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
                        } else {
                            future.channel().close();
                            businessHttp7.exceptionCaught(ctx, future.cause());
                            ReferenceCountUtil.release(request);
                            closeOnFlush(ctx.channel());
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
            String subProtocols = req.headers().get("Sec-WebSocket-Protocol");
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(req.uri(), subProtocols, true);
            this.handshake = wsFactory.newHandshaker(req);
            if (this.handshake == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                this.handshake.handshake(ctx.channel(), req);
                writeWebSocket(ctx, req);
            }
        } else {
            ReferenceCountUtil.retain(req);
            ctx.fireChannelRead(req);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (outboundChannel != null) {
            businessHttp7.close(ctx.channel());
            closeOnFlush(outboundChannel);
        } else {
            ctx.fireChannelInactive();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        businessHttp7.exceptionCaught(ctx, cause);
        closeOnFlush(ctx.channel());
    }

    private boolean isWebSocketRequest(Object msg) {
        HttpRequest req = (HttpRequest) msg;
        return req != null
                && req.decoderResult().isSuccess()
                && "websocket".equals(req.headers().get("Upgrade"));
    }
}
