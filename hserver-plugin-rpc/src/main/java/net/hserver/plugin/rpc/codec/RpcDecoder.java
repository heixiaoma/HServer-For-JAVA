package net.hserver.plugin.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.hserver.core.server.util.SerializationUtil;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {
    private Class<?> genericClass;

    public RpcDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() >= 16) {
            in.markReaderIndex();
            int r = in.readInt();
            int p = in.readInt();
            int c = in.readInt();
            if (r == 82 || p == 80 || c == 67) {
                int dataLength = in.readInt();
                if (in.readableBytes() < dataLength) {
                    in.resetReaderIndex();
                } else {
                    byte[] data = new byte[dataLength];
                    in.readBytes(data);
                    out.add(SerializationUtil.deserialize(data, this.genericClass));
                }
            }
        }
    }
}
