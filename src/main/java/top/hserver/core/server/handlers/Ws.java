package top.hserver.core.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Getter;
import lombok.Setter;

/**
 * @author hxm
 */
@Getter
@Setter
public class Ws {
    private ChannelHandlerContext ctx;
    private String message;
    private String uid;

    public Ws(ChannelHandlerContext ctx, String uid) {
        this.ctx = ctx;
        this.uid = uid;
    }

    public Ws(ChannelHandlerContext ctx, String message, String uid) {
        this.ctx = ctx;
        this.message = message;
        this.uid = uid;
    }

    public void send(String msg) {
        this.ctx.writeAndFlush(new TextWebSocketFrame(msg));
    }
}
