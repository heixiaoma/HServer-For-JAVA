package top.hserver.core.server.handlers;


import io.netty.handler.codec.http.HttpResponseStatus;
import top.hserver.core.server.context.StaticFile;
import top.hserver.core.server.context.HServerContext;
import top.hserver.core.server.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * 静态文件的处理，包括文件缓存效果等
 */

@Slf4j
public class StaticHandler {

  public StaticFile handler(String uri, HServerContext hServerContext) {
    //判断一次文件是否有/index.html文件
    if ("/".equals(uri)) {
      uri = "/index.html";
    }
    String basePath = "/static";
    InputStream input = getResourceStreamFromJar(basePath + uri);
    if (input != null) {
      return buildStaticFile(input, uri, hServerContext);
    }
    return null;
  }


  private InputStream getResourceStreamFromJar(String uri) {
    return StaticHandler.class.getResourceAsStream(uri);
  }

  /**
   * 构建一个静态文件对象
   *
   * @param input
   * @param url
   * @return
   */
  private StaticFile buildStaticFile(InputStream input, String url, HServerContext hServerContext) {
    StaticFile staticFile = null;
    try {
      //获取文件大小
      int available = input.available();
      staticFile = new StaticFile();
      staticFile.setSize(available);
      //获取文件名
      int i = url.lastIndexOf("/");
      int i1 = url.lastIndexOf(".");
      if (i > -1 && i1 > 0) {
        String fileName = url.substring(i + 1, url.length());
        String[] split = fileName.split("\\.");
        staticFile.setFileName(fileName);
        //设置文件是下载还
        staticFile.setFileType(split[split.length - 1]);
      } else {
        return null;
      }
      staticFile.setInputStream(input);
    } catch (Exception e) {
      throw new BusinessException(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "获取文件大小异常",e,hServerContext.getWebkit());
    }
    return staticFile;
  }
}
