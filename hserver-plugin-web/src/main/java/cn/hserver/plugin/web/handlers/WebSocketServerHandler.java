package cn.hserver.plugin.web.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.hserver.plugin.web.interfaces.WebSocketHandler;
import cn.hserver.core.ioc.IocUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.ReferenceCountUtil;
import cn.hserver.plugin.web.context.WsType;
import cn.hserver.core.server.util.ByteBufUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hxm
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger log = LoggerFactory.getLogger(WebSocketServerHandler.class);

    public static final Map<String, String> WEB_SOCKET_ROUTER = new ConcurrentHashMap<>();
    //处理多数据
    private StringBuilder frameBuffer = null;
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
                this.webSocketHandler.onConnect(new Ws(ctx, uid, request, WsType.INIT));
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
            return;
        } else if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        } else if (frame instanceof TextWebSocketFrame) {
            if (frame.isFinalFragment()) {
                this.webSocketHandler.onMessage(new Ws(ctx, ((TextWebSocketFrame) frame).text(), uid, request, WsType.TEXT));
                return;
            } else {
                frameBuffer = new StringBuilder();
                frameBuffer.append(((TextWebSocketFrame) frame).text());
            }
        } else if (frame instanceof BinaryWebSocketFrame) {
            this.webSocketHandler.onMessage(new Ws(ctx, ByteBufUtil.byteBufToBytes(frame.content()), uid, request, WsType.BINARY));
            return;
        } else if (frame instanceof ContinuationWebSocketFrame) {
            if (frameBuffer != null) {
                frameBuffer.append(((ContinuationWebSocketFrame) frame).text());
            } else {
                log.warn("ContinuationWebSocketFrame 帧不完整，缓存出现了null");
            }
        }
        if (frame.isFinalFragment()) {
            this.webSocketHandler.onMessage(new Ws(ctx, frameBuffer.toString(), uid, request, WsType.TEXT));
            frameBuffer = null;
        }

    }

    private boolean isWebSocketRequest(HttpRequest req) {
        return req != null
                && WEB_SOCKET_ROUTER.get(getReqUri(req)) != null
                && req.decoderResult().isSuccess()
                && "websocket".equals(req.headers().get("Upgrade"));
    }
}
