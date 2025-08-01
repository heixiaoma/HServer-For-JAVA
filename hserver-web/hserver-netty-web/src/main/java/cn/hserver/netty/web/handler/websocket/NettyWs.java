package cn.hserver.netty.web.handler.websocket;

import cn.hserver.mvc.constants.WsType;
import cn.hserver.mvc.request.Request;
import cn.hserver.mvc.websoket.Ws;
import cn.hserver.netty.web.context.HttpRequest;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.function.Consumer;

public class NettyWs extends Ws {

    private final ChannelHandlerContext ctx;

    public NettyWs(ChannelHandlerContext ctx, String uid, Request request, WsType type) {
        super(null,null,uid,request,type);
        this.ctx = ctx;
    }

    public NettyWs(ChannelHandlerContext ctx, byte[] array, String uid, HttpRequest request, WsType wsType) {
        super(array,null, uid, request, wsType);
        this.ctx = ctx;
    }
    public NettyWs(ChannelHandlerContext ctx, String message, String uid, HttpRequest request, WsType wsType) {
        super(null,message, uid, request, wsType);
        this.ctx = ctx;
    }


    @Override
    public void send(String msg, Consumer<Boolean> callback) {
        ChannelFuture channelFuture = this.ctx.writeAndFlush(new TextWebSocketFrame(msg));
        if (callback != null) {
            channelFuture .addListener((ChannelFuture future) -> callback.accept(future.isSuccess()));
        }
    }

    @Override
    public void send(byte[] msg, Consumer<Boolean> callback) {
        ChannelFuture channelFuture = this.ctx.writeAndFlush(new TextWebSocketFrame(Unpooled.wrappedBuffer(msg)));
        if (callback != null) {
            channelFuture .addListener((ChannelFuture future) -> callback.accept(future.isSuccess()));
        }
    }

    @Override
    public void sendBinary(byte[] msg, Consumer<Boolean> callback) {
        ChannelFuture channelFuture = this.ctx.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(msg)));
        if (callback != null) {
            channelFuture .addListener((ChannelFuture future) -> callback.accept(future.isSuccess()));
        }
    }
}
