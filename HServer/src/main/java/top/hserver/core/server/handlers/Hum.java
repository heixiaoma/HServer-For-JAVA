package top.hserver.core.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import top.hserver.core.server.context.HumMessage;
import top.hserver.core.server.util.HumMessageUtil;

public class Hum {
    private final DatagramPacket datagramPacket;
    private final ChannelHandlerContext ctx;
    private final Type type;

    public Hum(DatagramPacket datagramPacket, ChannelHandlerContext ctx, Type type) {
        this.datagramPacket = datagramPacket;
        this.ctx = ctx;
        this.type = type;
    }

    public void sendMessage(Object data) {
        ctx.writeAndFlush(new DatagramPacket(HumMessageUtil.createMessage(new HumMessage(data)), datagramPacket.sender()));
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        CLIENT, SERVER
    }

}
