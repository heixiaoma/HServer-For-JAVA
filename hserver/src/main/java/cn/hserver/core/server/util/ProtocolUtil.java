package cn.hserver.core.server.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.StringUtil;

public class ProtocolUtil {

    public static void print(ChannelHandlerContext ctx, String eventName, byte[] headers) {
        print(ctx, eventName, Unpooled.wrappedBuffer(headers));
    }

    public static void print(ChannelHandlerContext ctx, String eventName, ByteBuf msg) {
        String chStr = ctx.channel().toString();
        int length = msg.readableBytes();
        if (length == 0) {
            System.out.println(chStr + ' ' + eventName + ": 0B");
        } else {
            int outputLength = chStr.length() + 1 + eventName.length() + 2 + 10 + 1;
            int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
            int hexDumpLength = 2 + rows * 80;
            outputLength += hexDumpLength;

            StringBuilder buf = new StringBuilder(outputLength);
            buf.append(chStr).append(' ').append(eventName).append(": ").append(length).append('B');
            buf.append(StringUtil.NEWLINE);
            ByteBufUtil.appendPrettyHexDump(buf, msg);
            System.out.println(buf);
        }
    }
}
