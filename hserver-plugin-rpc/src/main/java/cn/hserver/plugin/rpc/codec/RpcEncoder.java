package cn.hserver.plugin.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import cn.hserver.core.server.util.SerializationUtil;

public class RpcEncoder extends MessageToByteEncoder {
    private Class<?> genericClass;

    public RpcEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    protected void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) {
        if (this.genericClass.isInstance(in)) {
            byte[] data = SerializationUtil.serialize(in);
            out.writeInt(82);
            out.writeInt(80);
            out.writeInt(67);
            out.writeInt(data.length);
            out.writeBytes(data);
        }

    }
}
