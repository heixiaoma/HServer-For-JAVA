package top.hserver.core.server.util;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.*;

public class ByteBufUtil {

  public static ByteBuf FileToByteBuf(File file) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      int size = 0;
      FileInputStream input = new FileInputStream(file);
      ByteBuf byteBuf = Unpooled.buffer();
      byte[] buffer = new byte[1024];
      int len;
      while ((len = input.read(buffer)) > -1) {
        size += len;
        baos.write(buffer, 0, len);
      }
      baos.flush();
      ByteArrayInputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
      byteBuf.writeBytes(inputStream, size);
      input.close();
      inputStream.close();
      baos.close();
      return byteBuf;
    } catch (Exception e) {
      return null;
    }
  }

  public static ByteBuf FileToByteBuf(InputStream input) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      int size = 0;
      ByteBuf byteBuf = Unpooled.buffer();
      byte[] buffer = new byte[1024];
      int len;
      while ((len = input.read(buffer)) > -1) {
        size += len;
        baos.write(buffer, 0, len);
      }
      baos.flush();
      ByteArrayInputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
      byteBuf.writeBytes(inputStream, size);
      input.close();
      inputStream.close();
      baos.close();
      return byteBuf;
    } catch (Exception e) {
      return null;
    }
  }

}
