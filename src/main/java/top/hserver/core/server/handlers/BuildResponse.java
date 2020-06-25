package top.hserver.core.server.handlers;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import top.hserver.core.interfaces.HttpRequest;
import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.context.HServerContext;
import top.hserver.core.server.context.Response;
import top.hserver.core.server.exception.BusinessBean;
import top.hserver.core.server.exception.BusinessException;
import top.hserver.core.server.util.ByteBufUtil;
import top.hserver.core.server.util.ExceptionUtil;
import top.hserver.core.server.util.FreemarkerUtil;

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
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(Objects.requireNonNull(ByteBufUtil.fileToByteBuf(hServerContext.getStaticFile().getInputStream()))));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, hServerContext.getStaticFile().getFileHead() + ";charset=UTF-8");
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
            //getInputStream类型
            response = new DefaultFullHttpResponse(
                    HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(Objects.requireNonNull(ByteBufUtil.fileToByteBuf(response1.getInputStream()))));
        } else {
            //File类型
            response = new DefaultFullHttpResponse(
                    HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(Objects.requireNonNull(ByteBufUtil.fileToByteBuf(response1.getFile()))));
        }
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream;charset=UTF-8");
        response.headers().add(HttpHeaderNames.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", response1.getFileName()));
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
                Unpooled.wrappedBuffer(response1.getJsonAndHtml().getBytes(StandardCharsets.UTF_8)));
        return response;
    }


    /**
     * 构建控制器返回的数据
     *
     * @param hServerContext
     * @return
     */
    public static FullHttpResponse buildControllerResult(HServerContext hServerContext) {
        //是否是方法调用的
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(hServerContext.getResult().getBytes(StandardCharsets.UTF_8)));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
        return response;
    }


    /**
     * 对于head头相关的数据设置，做下收尾
     *
     * @param response
     * @param response1
     * @return
     */
    public static FullHttpResponse buildEnd(FullHttpResponse response, Response response1) {

        response.headers().set(HttpHeaderNames.SERVER, "HServer");
        response.headers().set("HServer", ConstConfig.VERSION);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        //用户自定义头头
        Map<String, String> headers = response1.getHeaders();
        headers.forEach((a, b) -> {
            response.headers().set(a, b);
            if (a.equals("location")) {
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
        BusinessBean build = BusinessBean.builder().args(httpRequest.getRequestParams().toString())
                .code(e.getHttpCode())
                .errorDesc(e.getErrorDescription())
                .errorMsg(ExceptionUtil.getHtmlMessage(e.getThrowable()))
                .method(httpRequest.getRequestType().name())
                .url(httpRequest.getUri())
                .version(ConstConfig.VERSION)
                .bugAddress(ConstConfig.BUG_ADDRESS)
                .communityAddress(ConstConfig.communityAddress)
                .build();

        Map data = new HashMap<>();
        data.put("business", build);
        String html = "模板输出错误";
        try {
            html = FreemarkerUtil.getTemplate("hserver_error.ftl", data);
        } catch (Exception e1) {
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
        response.headers().set("HServer", ConstConfig.VERSION);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        return response;
    }
}
