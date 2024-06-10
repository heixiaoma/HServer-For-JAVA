package cn.hserver.core.server.util;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.*;

/**
 * @author hxm
 */
public class ByteBufUtil {

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
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) != -1) {
                babs.write(buffer, 0, len);
            }
            babs.flush();
            byte[] byteArray = babs.toByteArray();
            ByteBuf byteBuf = Unpooled.wrappedBuffer(byteArray);
            input.close();
            babs.close();
            return byteBuf;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
