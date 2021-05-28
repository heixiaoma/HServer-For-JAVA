package top.hserver.cloud.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import top.hserver.cloud.util.SerializationUtil;

/**
 * @author hxm
 */
public class RpcEncoder extends MessageToByteEncoder {

    private Class<?> genericClass;

    public RpcEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) {
        if (genericClass.isInstance(in)) {
            byte[] data = SerializationUtil.serialize(in);
            //header RPC 82,80,67
            out.writeInt(82);
            out.writeInt(80);
            out.writeInt(67);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }

}
