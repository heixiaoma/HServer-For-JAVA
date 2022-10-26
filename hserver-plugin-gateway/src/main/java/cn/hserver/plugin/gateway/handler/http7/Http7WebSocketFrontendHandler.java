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

import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;


public class Http7WebSocketFrontendHandler extends ChannelInboundHandlerAdapter {

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
            Object in = businessHttp7.in(ctx, msg);
            if (in == null) {
                return;
            }
            outboundChannel.writeAndFlush(in);
        }
    }

    private void writeWebSocket(ChannelHandlerContext ctx, HttpRequest request) throws URISyntaxException {
        if (outboundChannel == null) {
            Bootstrap b = new Bootstrap();
            b.group(ctx.channel().eventLoop());

            SocketAddress proxyHost = businessHttp7.getProxyHost(ctx, request, ctx.channel().localAddress());
            if (!request.headers().contains(HttpHeaderNames.ORIGIN)) {
                request.headers().add(HttpHeaderNames.ORIGIN, proxyHost.toString()+request.uri());
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
            //数据代理服务选择器
            ChannelFuture f = b.connect(proxyHost).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    try {
                        handler.handshakeFuture().addListener((future1) -> {
                            future1.sync();
                            future.channel().writeAndFlush(request);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    future.channel().close();
                    ReferenceCountUtil.release(request);
                }
            });
            outboundChannel = f.channel();
        } else {
            read(ctx, request);
        }

    }

    private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req) throws Exception {
        if (isWebSocketRequest(req)) {
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(req.uri(), null, true);
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