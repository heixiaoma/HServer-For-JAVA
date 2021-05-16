package top.hserver.core.client;

import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import top.hserver.core.interfaces.WebSocketClientHandler;
import top.hserver.core.ioc.IocUtil;
import top.hserver.core.server.handlers.Wsc;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author hxm
 */
@ChannelHandler.Sharable
public class HServerWebSocketClientHandler extends SimpleChannelInboundHandler<Object> {

    public static final Set<String> beanName = new HashSet<>();

    private final WebSocketClientHandshaker handshaker;

    private ChannelPromise handshakeFuture;

    private WebSocketClientHandler webSocketClientHandler;


    private HServerWebSocketClient.ImConnection imConnection;


    public void setImConnection(HServerWebSocketClient.ImConnection imConnection) {
        this.imConnection = imConnection;
    }

    public HServerWebSocketClientHandler(String name, WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
        this.webSocketClientHandler = (WebSocketClientHandler) IocUtil.getBean(name);

    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handshaker.handshake(ctx.channel());
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            try {
                handshaker.finishHandshake(ch, (FullHttpResponse) msg);
                webSocketClientHandler.onConnect(new Wsc(ctx));
                handshakeFuture.setSuccess();
            } catch (WebSocketHandshakeException e) {
                webSocketClientHandler.throwable(new Wsc(ctx), e);
                handshakeFuture.setFailure(e);
            }
            return;
        }

        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            return;
        }

        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            webSocketClientHandler.onMessage(new Wsc(ctx, textFrame));
        } else if (frame instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame) frame;
            webSocketClientHandler.onMessage(new Wsc(ctx, binaryWebSocketFrame));
        } else if (frame instanceof PongWebSocketFrame) {
        } else if (frame instanceof CloseWebSocketFrame) {
            webSocketClientHandler.disConnect(new Wsc(ctx));
            ch.close();
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        webSocketClientHandler.throwable(new Wsc(ctx), cause);
        handshakeFuture.setFailure(cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        webSocketClientHandler.disConnect(new Wsc(ctx));
        imConnection.doConnect();
    }

}
