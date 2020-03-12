package top.hserver.core.server.handlers;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.context.Response;
import top.hserver.core.server.context.WebContext;
import top.hserver.core.server.exception.BusinessException;
import top.hserver.core.server.util.ByteBufUtil;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


/**
 * 构建返回对象的数据工具集合
 */
public class BuildResponse {

  /**
   * 静态文件
   *
   * @param webContext
   * @return
   */
  public static FullHttpResponse buildStaticShowType(WebContext webContext) {
    FullHttpResponse response = new DefaultFullHttpResponse(
      HTTP_1_1,
      HttpResponseStatus.OK,
      Unpooled.wrappedBuffer(Objects.requireNonNull(ByteBufUtil.FileToByteBuf(webContext.getStaticFile().getInputStream()))));
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, webContext.getStaticFile().getFileHead() + ";charset=UTF-8");
    return response;
  }

  /**
   * 控制下载类型的文件
   *
   * @param webContext
   * @return
   */
  public static FullHttpResponse buildControllerDownloadType(WebContext webContext) {
    Response response1 = webContext.getResponse();
    FullHttpResponse response;
    if (response1.getFile() == null) {
      //getInputStream类型
      response = new DefaultFullHttpResponse(
        HTTP_1_1,
        HttpResponseStatus.OK,
        Unpooled.wrappedBuffer(Objects.requireNonNull(ByteBufUtil.FileToByteBuf(response1.getInputStream()))));
    } else {
      //File类型
      response = new DefaultFullHttpResponse(
        HTTP_1_1,
        HttpResponseStatus.OK,
        Unpooled.wrappedBuffer(Objects.requireNonNull(ByteBufUtil.FileToByteBuf(response1.getFile()))));
    }
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream;charset=UTF-8");
    response.headers().add(HttpHeaderNames.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", webContext.getResponse().getFileName()));
    return response;
  }


  /**
   * HttpResponse对象中设置的数据
   *
   * @return
   */
  public static FullHttpResponse buildHttpResponseData(WebContext webContext) {
    FullHttpResponse response = new DefaultFullHttpResponse(
      HTTP_1_1,
      HttpResponseStatus.OK,
      Unpooled.wrappedBuffer(webContext.getResponse().getJsonAndHtml().getBytes(StandardCharsets.UTF_8)));
    return response;
  }


  /**
   * 构建控制器返回的数据
   *
   * @param webContext
   * @return
   */
  public static FullHttpResponse buildControllerResult(WebContext webContext) {
    //是否是方法调用的
    FullHttpResponse response = new DefaultFullHttpResponse(
      HTTP_1_1,
      HttpResponseStatus.OK,
      Unpooled.wrappedBuffer(webContext.getResult().getBytes(StandardCharsets.UTF_8)));
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
    return response;
  }


  /**
   * 对于head头相关的数据设置，做下收尾
   *
   * @param webContext
   * @return
   */
  public static FullHttpResponse buildEnd(FullHttpResponse response, WebContext webContext) {

    response.headers().set(HttpHeaderNames.SERVER, "HServer");
    response.headers().set("HServer", ConstConfig.version);
    response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
    response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
    //用户自定义头头
    Map<String, String> headers = webContext.getResponse().getHeaders();
    headers.forEach((a, b) -> {
      response.headers().set(a, b);
      if (a.equals("location")) {
        response.setStatus(FOUND);
      }
    });
    webContext.stopStatistics(System.currentTimeMillis());
    return response;
  }


  /**
   * 报错的处理
   *
   * @param e
   * @return
   */
  public static FullHttpResponse buildError(Throwable e) {
    BusinessException cause = (BusinessException) e.getCause();
    HttpResponseStatus httpResponseStatus = HttpResponseStatus.valueOf(cause.getHttpCode());
    FullHttpResponse response = new DefaultFullHttpResponse(
      HTTP_1_1,
      httpResponseStatus,
      Unpooled.wrappedBuffer(cause.getRespMsg().getBytes(StandardCharsets.UTF_8)));
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
    response.headers().set("HServer", ConstConfig.version);
    response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
    response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

    return response;
  }


}
