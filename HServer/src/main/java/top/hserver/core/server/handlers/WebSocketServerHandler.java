package top.hserver.core.server.handlers;

import top.hserver.core.interfaces.WebSocketHandler;
import top.hserver.core.ioc.IocUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.ReferenceCountUtil;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hxm
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    public static final Map<String, String> WebSocketRouter = new ConcurrentHashMap<>();

    private WebSocketServerHandshaker handshake;
    private WebSocketHandler webSocketHandler;
    private String uri;
    private String uid;
    private HttpRequest request;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest) {
            handleHttpRequest(ctx, (HttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            initHandler();
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        } else {
            ReferenceCountUtil.retain(msg);
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req) {
        if (isWebSocketRequest(req)) {
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(req.uri(), null, true);
            this.handshake = wsFactory.newHandshaker(req);
            if (this.handshake == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                this.handshake.handshake(ctx.channel(), req);
                this.uri =getReqUri(req);
                this.request = req;
                this.uid = UUID.randomUUID().toString();
                initHandler();
                CompletableFuture.completedFuture(new Ws(ctx, uid, request))
                        .thenAcceptAsync(this.webSocketHandler::onConnect, ctx.executor());
            }
        } else {
            ReferenceCountUtil.retain(req);
            ctx.fireChannelRead(req);
        }
    }

    private String getReqUri(HttpRequest req) {
        int i = req.uri().indexOf("?");
        if (i > 0) {
            String uri = req.uri();
            return uri.substring(0, i);
        } else {
            return req.uri();
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof CloseWebSocketFrame) {
            this.handshake.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            CompletableFuture.completedFuture(new Ws(ctx, uid, request))
                    .thenAcceptAsync(this.webSocketHandler::disConnect, ctx.executor());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException("unsupported frame type: " + frame.getClass().getName());
        }
        CompletableFuture.completedFuture(new Ws(ctx, ((TextWebSocketFrame) frame).text(), uid, request))
                .thenAcceptAsync(this.webSocketHandler::onMessage, ctx.executor());
    }

    private boolean isWebSocketRequest(HttpRequest req) {
        return req != null
                && WebSocketRouter.get(getReqUri(req)) != null
                && req.decoderResult().isSuccess()
                && "websocket".equals(req.headers().get("Upgrade"));
    }

    private void initHandler() {
        this.webSocketHandler = (WebSocketHandler) IocUtil.getBean(WebSocketRouter.get(uri));
    }
}