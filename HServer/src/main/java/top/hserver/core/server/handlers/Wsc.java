package top.hserver.core.server.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.*;

/**
 * @author hxm
 */
public class Wsc {
    private ChannelHandlerContext ctx;
    private WebSocketFrame webSocketFrame;

    public Wsc(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public Wsc(ChannelHandlerContext ctx, WebSocketFrame webSocketFrame) {
        this.ctx = ctx;
        this.webSocketFrame = webSocketFrame;
    }

    public void send(String msg) {
        this.ctx.writeAndFlush(new TextWebSocketFrame(msg));
    }

    public void send(byte[] msg) {
        this.ctx.writeAndFlush(new TextWebSocketFrame(Unpooled.wrappedBuffer(msg)));
    }

    public void sendClose() {
        this.ctx.writeAndFlush(new CloseWebSocketFrame());
    }

    public void sendPing() {
        WebSocketFrame frame = new PingWebSocketFrame(Unpooled.wrappedBuffer(new byte[]{8, 1, 8, 1}));
        this.ctx.writeAndFlush(frame);
    }

    public void sendBinary(byte[] msg) {
        this.ctx.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(msg)));
    }

    public void sendBinary(ByteBuf msg) {
        this.ctx.writeAndFlush(new BinaryWebSocketFrame(msg));
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public WebSocketFrame getWebSocketFrame() {
        return webSocketFrame;
    }

    public void setWebSocketFrame(WebSocketFrame webSocketFrame) {
        this.webSocketFrame = webSocketFrame;
    }

    public String getText() {
        if (webSocketFrame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) webSocketFrame;
            return textFrame.text();
        }
        return null;
    }

    public BinaryWebSocketFrame getBinary() {
        if (webSocketFrame instanceof BinaryWebSocketFrame) {
            return (BinaryWebSocketFrame) webSocketFrame;
        }
        return null;

    }

}
