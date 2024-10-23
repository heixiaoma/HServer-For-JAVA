package cn.hserver.plugin.web.handlers;

import cn.hserver.core.server.context.ConstConfig;
import cn.hserver.plugin.web.context.*;
import cn.hserver.plugin.web.util.FreemarkerUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import cn.hserver.plugin.web.interfaces.HttpRequest;
import cn.hserver.plugin.web.exception.BusinessBean;
import cn.hserver.plugin.web.exception.BusinessException;
import cn.hserver.core.server.util.ByteBufUtil;
import cn.hserver.core.server.util.ExceptionUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


/**
 * 构建返回对象的数据工具集合
 *
 * @author hxm
 */
public class BuildResponse {

    /**
     * 静态文件
     *
     * @param hServerContext
     * @return
     */
    public static FullHttpResponse buildStaticShowType(HServerContext hServerContext) {
        InputStream inputStream = hServerContext.getStaticFile().getInputStream();
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(Objects.requireNonNull(ByteBufUtil.fileToByteBuf(inputStream))));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, hServerContext.getStaticFile().getFileHead() + ";charset=UTF-8");
        try {
            inputStream.close();
        } catch (IOException ignored) {
        }
        return response;
    }

    /**
     * 控制下载类型的文件
     *
     * @param response1
     * @return
     */
    public static FullHttpResponse buildControllerDownloadType(Response response1) {
        FullHttpResponse response;
        if (response1.getFile() == null) {
            InputStream inputStream = response1.getInputStream();
            //getInputStream类型
            response = new DefaultFullHttpResponse(
                    HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(Objects.requireNonNull(ByteBufUtil.fileToByteBuf(inputStream))));
            try {
                inputStream.close();
            } catch (IOException ignored) {
            }
        } else {
            //File类型
            response = new DefaultFullHttpResponse(
                    HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(Objects.requireNonNull(ByteBufUtil.fileToByteBuf(response1.getFile()))));
        }

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, MimeType.getFileType(response1.getFileName()) + ";charset=UTF-8");
        //attachment下载模式，inline预览模式
        response.headers().add(HttpHeaderNames.CONTENT_DISPOSITION, String.format("inline; filename=\"%s\"", response1.getFileName()));
        return response;
    }


    /**
     * HttpResponse对象中设置的数据
     *
     * @return
     */
    public static FullHttpResponse buildHttpResponseData(Response response1) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(response1.getResult().getBytes(StandardCharsets.UTF_8)));
        return response;
    }


    /**
     * 对于head头相关的数据设置，做下收尾
     *
     * @param response
     * @param response1
     * @return
     */
    public static FullHttpResponse buildEnd(Request request, FullHttpResponse response, Response response1) {
        if (response1.getHttpResponseStatus() != null) {
            response.setStatus(response1.getHttpResponseStatus());
        }
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        //用户自定义头头
        Map<String, String> headers = response1.getHeaders();
        headers.forEach((a, b) -> {
            response.headers().set(a, b);
            if (a.equalsIgnoreCase("location")) {
                response.setStatus(FOUND);
            }
        });
        return response;
    }


    public static FullHttpResponse buildString(String res) {
        HttpResponseStatus httpResponseStatus = HttpResponseStatus.valueOf(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        return getFullHttpResponse(res, httpResponseStatus);
    }


    /**
     * 报错的处理
     *
     * @param e
     * @return
     */
    public static FullHttpResponse buildError(BusinessException e) {
        HttpRequest httpRequest = e.getWebkit().httpRequest;
        BusinessBean build = new BusinessBean();
        build.setArgs(httpRequest.getRequestParams().toString());
        build.setCode(e.getHttpCode());
        build.setErrorDesc(e.getErrorDescription());
        build.setErrorMsg(ExceptionUtil.getHtmlMessage(e.getThrowable()));
        build.setMethod(httpRequest.getRequestType().name());
        build.setUrl(httpRequest.getUri());
        build.setVersion(ConstConfig.VERSION);
        build.setBugAddress(WebConstConfig.BUG_ADDRESS);

        Map<Object, Object> data = new HashMap<>();
        data.put("business", build);
        String html = "模板输出错误";
        try {
            html = FreemarkerUtil.getTemplate("hserver_error.ftl", data);
        } catch (Exception ignored) {
        }
        HttpResponseStatus httpResponseStatus = HttpResponseStatus.valueOf(e.getHttpCode());
        return getFullHttpResponse(html, httpResponseStatus);
    }

    public static FullHttpResponse buildError(Throwable e) {
        HttpResponseStatus httpResponseStatus = HttpResponseStatus.valueOf(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        String message = ExceptionUtil.getMessage(e);
        return getFullHttpResponse(message, httpResponseStatus);
    }

    private static FullHttpResponse getFullHttpResponse(String html, HttpResponseStatus httpResponseStatus) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1,
                httpResponseStatus,
                Unpooled.wrappedBuffer(html.getBytes(StandardCharsets.UTF_8)));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        return response;
    }

    public static void writeException(ChannelHandlerContext ctx, Throwable cause) {
        writeException(ctx, cause, HttpResponseStatus.SERVICE_UNAVAILABLE);
    }

    private static void writeException(ChannelHandlerContext ctx, Throwable cause, HttpResponseStatus status) {
        String message = ExceptionUtil.getMessage(cause);
        message = WebConstConfig.SERVER_NAME+":" + ConstConfig.VERSION + "服务器异常:\n" + message;

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                status,
                Unpooled.wrappedBuffer(message.getBytes(StandardCharsets.UTF_8)));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        HServerContextHolder.remove();
    }

}
