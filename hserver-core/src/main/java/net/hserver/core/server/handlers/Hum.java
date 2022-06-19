package net.hserver.core.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import net.hserver.core.server.context.HumMessage;
import net.hserver.core.server.util.HumMessageUtil;

public class Hum {
    private final DatagramPacket datagramPacket;
    private final ChannelHandlerContext ctx;
    private final Type type;

    public Hum(DatagramPacket datagramPacket, ChannelHandlerContext ctx, Type type) {
        this.datagramPacket = datagramPacket;
        this.ctx = ctx;
        this.type = type;
    }

    public boolean isLive(){
        return ctx.channel().isActive()&&!ctx.isRemoved()&&ctx.channel().isOpen();
    }

    public void sendMessage(HumMessage humMessage) {
        ctx.writeAndFlush(new DatagramPacket(HumMessageUtil.createMessage(humMessage), datagramPacket.sender()));
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        CLIENT, SERVER
    }

}
