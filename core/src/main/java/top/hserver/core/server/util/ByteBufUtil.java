package top.hserver.core.server.util;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.*;

/**
 * @author hxm
 */
public class ByteBufUtil {

    public static byte[] byteBufToBytes(ByteBuf buf) {
        int length =buf.readableBytes();
        byte[] body =new byte[length];
        buf.readBytes(body);
        return body;
    }

    public static ByteBuf fileToByteBuf(File file) {
        try {
            FileInputStream input = new FileInputStream(file);
            return fileToByteBuf(input);
        } catch (Exception e) {
            return null;
        }
    }

    public static ByteBuf fileToByteBuf(InputStream input) {
        try {
            ByteArrayOutputStream babs = new ByteArrayOutputStream();
            int size = 0;
            ByteBuf byteBuf = Unpooled.buffer();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) > -1) {
                size += len;
                babs.write(buffer, 0, len);
            }
            babs.flush();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(babs.toByteArray());
            byteBuf.writeBytes(inputStream, size);
            input.close();
            inputStream.close();
            babs.close();
            return byteBuf;
        } catch (Exception e) {
            return null;
        }
    }

}
