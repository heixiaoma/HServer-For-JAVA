package net.hserver.core.server.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.hserver.core.server.context.HumMessage;

public class HumMessageUtil {
    public static ByteBuf createMessage(HumMessage udpMessage) {
        ByteBuf emptyBuffer = Unpooled.buffer();
        byte[] data = SerializationUtil.serialize(udpMessage);
        //H(hserver) U（udp） M（message）
        emptyBuffer.writeInt(72);
        emptyBuffer.writeInt(85);
        emptyBuffer.writeInt(77);
        emptyBuffer.writeInt(data.length);
        emptyBuffer.writeBytes(data);
        return emptyBuffer;
    }

    public static HumMessage getMessage(ByteBuf in) {
        int i = in.readableBytes();
        if (i >= 16) {
            in.markReaderIndex();
            int h = in.readInt();
            int u = in.readInt();
            int m = in.readInt();
            if (h == 72 || u == 85 || m == 77) {
                int dataLength = in.readInt();
                if (in.readableBytes() < dataLength) {
                    in.resetReaderIndex();
                } else {
                    byte[] data = new byte[dataLength];
                    in.readBytes(data);
                    return SerializationUtil.deserialize(data, HumMessage.class);
                }
            }
        }
        return null;
    }

}
