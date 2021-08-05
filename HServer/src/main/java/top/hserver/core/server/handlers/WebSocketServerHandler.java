package top.hserver.core.server.handlers;

import top.hserver.core.interfaces.WebSocketHandler;
import top.hserver.core.ioc.IocUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.ReferenceCountUtil;
import top.hserver.core.server.context.WsType;
import top.hserver.core.server.util.ByteBufUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hxm
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    public static final Map<String, String> WEB_SOCKET_ROUTER = new ConcurrentHashMap<>();

    private WebSocketServerHandshaker handshake;
    private WebSocketHandler webSocketHandler;
    private String uid;
    private HttpRequest request;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest) {
            /**
             * 看看是不是第一次握手。
             */
            handleHttpRequest(ctx, (HttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
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

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        if (request != null && uid != null) {
            this.webSocketHandler.disConnect(new Ws(ctx, uid, request, WsType.CLOSE));
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req) {
        if (isWebSocketRequest(req)) {
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(req.uri(), null, true);
            this.handshake = wsFactory.newHandshaker(req);
            if (this.handshake == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                this.handshake.handshake(ctx.channel(), req);
                String uri = getReqUri(req);
                this.request = req;
                this.uid = ctx.channel().id().asLongText();
                this.webSocketHandler = (WebSocketHandler) IocUtil.getBean(WEB_SOCKET_ROUTER.get(uri));
                this.webSocketHandler.onConnect(new Ws(ctx, uid, request,WsType.INIT));
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
        } else if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
        } else if (frame instanceof TextWebSocketFrame) {
            this.webSocketHandler.onMessage(new Ws(ctx, ((TextWebSocketFrame) frame).text(), uid, request,WsType.TEXT));
        } else if (frame instanceof BinaryWebSocketFrame) {
            this.webSocketHandler.onMessage(new Ws(ctx, ByteBufUtil.byteBufToBytes(frame.content()), uid, request,WsType.BINARY));
        }
    }

    private boolean isWebSocketRequest(HttpRequest req) {
        return req != null
                && WEB_SOCKET_ROUTER.get(getReqUri(req)) != null
                && req.decoderResult().isSuccess()
                && "websocket".equals(req.headers().get("Upgrade"));
    }
}