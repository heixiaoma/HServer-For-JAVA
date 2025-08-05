package cn.hserver.netty.web.handler.websocket;

import cn.hserver.mvc.constants.WsType;
import cn.hserver.mvc.request.HeadMap;
import cn.hserver.mvc.websoket.WebSocketHandler;
import cn.hserver.mvc.websoket.Ws;
import cn.hserver.netty.web.constants.NettyConfig;
import cn.hserver.netty.web.handler.NettyServerHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author hxm
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger log = LoggerFactory.getLogger(WebSocketServerHandler.class);

    //处理多数据
    private StringBuilder frameBuffer = null;
    private ByteArrayOutputStream byteArrayOutputStream = null;
    private WebSocketServerHandshaker handshake;
    private WebSocketHandler webSocketHandler;
    private String uid;
    private cn.hserver.netty.web.context.HttpRequest request;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws IOException {
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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(),cause);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        if (request != null && uid != null) {
            this.webSocketHandler.disConnect(new NettyWs(ctx, uid, request, WsType.CLOSE));
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req) {
        if (isWebSocketRequest(req)) {
            String uri = getReqUri(req);
            this.request = handlerReq(req);
            this.uid = ctx.channel().id().asLongText();
            this.webSocketHandler = NettyServerHandler.WEB_SOCKET_ROUTER.get(uri);
            if (webSocketHandler==null){
                log.error("未找到对应的WebSocketHandler:{}",req.uri());
                ReferenceCountUtil.retain(req);
                ctx.fireChannelRead(req);
                return;
            }
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(req.uri(),
                    this.webSocketHandler.getSubProtocols(),
                    true,
                    NettyConfig.MAX_WEBSOCKET_FRAME_LENGTH
            );
            this.handshake = wsFactory.newHandshaker(req);
            if (this.handshake == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                this.handshake.handshake(ctx.channel(), req);

                this.webSocketHandler.onConnect(new NettyWs(ctx, uid,request, WsType.INIT));
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


    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws IOException {
        if (frame instanceof CloseWebSocketFrame) {
            this.handshake.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        } else if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        } else if (frame instanceof TextWebSocketFrame) {
            if (frame.isFinalFragment()) {
                this.webSocketHandler.onMessage(new NettyWs(ctx, ((TextWebSocketFrame) frame).text(), uid, request, WsType.TEXT));
                return;
            } else {
                frameBuffer = new StringBuilder();
                frameBuffer.append(((TextWebSocketFrame) frame).text());
            }
        } else if (frame instanceof BinaryWebSocketFrame) {
            if (frame.isFinalFragment()) {
                this.webSocketHandler.onMessage(new NettyWs(ctx, frame.content().array(), uid, request, WsType.BINARY));
                return;
            } else {
                byteArrayOutputStream = new ByteArrayOutputStream();
                byteArrayOutputStream.write(frame.content().array());
            }
        } else if (frame instanceof ContinuationWebSocketFrame) {
            if (frameBuffer != null) {
                frameBuffer.append(((ContinuationWebSocketFrame) frame).text());
            } else if (byteArrayOutputStream != null) {
                byteArrayOutputStream.write(frame.content().array());
                byteArrayOutputStream.flush();
            } else {
                log.warn("ContinuationWebSocketFrame 帧不完整，缓存出现了null");
            }
        }
        if (frame.isFinalFragment()) {
            if (frameBuffer != null) {
                this.webSocketHandler.onMessage(new NettyWs(ctx, frameBuffer.toString(), uid,request, WsType.TEXT));
                frameBuffer = null;
            } else if (byteArrayOutputStream != null) {
                byte[] bytes = byteArrayOutputStream.toByteArray();
                this.webSocketHandler.onMessage(new NettyWs(ctx, bytes, uid, request, WsType.BINARY));
                byteArrayOutputStream.close();
                byteArrayOutputStream = null;
            }
        }
    }

    private boolean isWebSocketRequest(HttpRequest req) {
        return req != null
                && NettyServerHandler.WEB_SOCKET_ROUTER.containsKey(getReqUri(req))
                && req.decoderResult().isSuccess()
                && "websocket".equals(req.headers().get("Upgrade"));
    }


    private cn.hserver.netty.web.context.HttpRequest handlerReq(HttpRequest req) {
        cn.hserver.netty.web.context.HttpRequest request = new cn.hserver.netty.web.context.HttpRequest();
        try {
            request.setUriWithParams(req.uri());
            //获取URi，設置真實的URI
            int i = req.uri().indexOf("?");
            if (i > 0) {
                String uri = req.uri();
                request.setUri(uri.substring(0, i));
            } else {
                request.setUri(req.uri());
            }
            request.setKeepAlive(HttpUtil.isKeepAlive(req));
            request.setRequestType(req.method());
            HeadMap headers=new HeadMap();
            req.headers().names().forEach(a -> headers.put(a, req.headers().get(a)));
            request.setHeaders(headers);
            Map<String, List<String>> requestParams = request.getRequestParams();
            QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
            Map<String, List<String>> params = decoder.parameters();
            for (Map.Entry<String, List<String>> next : params.entrySet()) {
                requestParams.put(next.getKey(), next.getValue());
                for (String s : next.getValue()) {
                    request.addReqUrlParams(next.getKey(), s);
                }
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return request;
    }

}
