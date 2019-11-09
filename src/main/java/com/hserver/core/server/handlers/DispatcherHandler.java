package com.hserver.core.server.handlers;

import com.alibaba.fastjson.JSON;
import com.hserver.core.ioc.IocUtil;
import com.hserver.core.server.context.Request;
import com.hserver.core.server.context.StaticFile;
import com.hserver.core.server.context.WebContext;
import com.hserver.core.server.exception.BusinessException;
import com.hserver.core.server.router.RouterInfo;
import com.hserver.core.server.router.RouterManager;
import com.hserver.util.ExceptionUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import lombok.extern.slf4j.Slf4j;
import sun.rmi.runtime.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;

/**
 * 分发器
 */
@Slf4j
public class DispatcherHandler {

    private final static StaticHandler staticHandler = new StaticHandler();

    //标识不是静态文件，这样下次使用方便直接跳过检查
    private final static CopyOnWriteArraySet<String> noStaticFileUri = new CopyOnWriteArraySet<>();

    public static WebContext buildWebContext(ChannelHandlerContext ctx,
                                             HttpRequest req) {


        WebContext webContext = new WebContext();
        Request request = new Request();
        Map<String, String> requestParams = new HashMap<>();

        //如果GET请求
        if (req.method() == GET) {
            QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
            Map<String, List<String>> parame = decoder.parameters();
            Iterator<Map.Entry<String, List<String>>> iterator = parame.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, List<String>> next = iterator.next();
                requestParams.put(next.getKey(), next.getValue().get(0));
            }
            request.setRequestType(GET);
        }

        //如果POST请求
        if (req.method() == POST) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(
                    new DefaultHttpDataFactory(false), req);
            List<InterfaceHttpData> postData = decoder.getBodyHttpDatas(); //
            for (InterfaceHttpData data : postData) {
                if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                    MemoryAttribute attribute = (MemoryAttribute) data;
                    requestParams.put(attribute.getName(), attribute.getValue());
                }
            }
            request.setRequestType(POST);
        }
        //获取URi
        int i = req.uri().indexOf("?");
        if (i > 0) {
            String uri = req.uri();
            request.setUri(uri.substring(0, i));
        } else {
            request.setUri(req.uri());
        }
        request.setRequestParams(requestParams);
        webContext.setRequest(request);
        return webContext;
    }

    /**
     * 统计
     *
     * @param webContext
     * @return
     */
    public static WebContext Statistics(WebContext webContext) {
        return webContext;
    }

    /**
     * 静态文件的处理
     *
     * @param webContext
     * @return
     */
    public static WebContext staticFile(WebContext webContext) {
        //检查是不是静态文件，如果是封装下请求，然后跳过控制器的方法
        if (noStaticFileUri.contains(webContext.getRequest().getUri())) {
            return webContext;
        }
        StaticFile handler = staticHandler.handler(webContext.getRequest().getUri());
        if (handler != null) {
            webContext.setStaticFile(true);
            webContext.setStaticFile(handler);
        } else {
            noStaticFileUri.add(webContext.getRequest().getUri());
        }
        return webContext;
    }

    /**
     * 去执行控制器的方法
     *
     * @param webContext
     * @return
     */
    public static WebContext findController(WebContext webContext) {

        /**
         * 如果静态文件就跳过当前的处理，否则就去执行控制器的方法
         */
        if (webContext.isStaticFile()) {
            return webContext;
        }

        RouterInfo routerInfo = RouterManager.getRouterInfo(webContext.getRequest().getUri(), GET);
        if (routerInfo == null) {
            log.error("为找到对应的控制器");
            throw new BusinessException(404, "为找到对应的控制器");
        }
        try {
            Method method = routerInfo.getMethod();
            Class<?> aClass = routerInfo.getaClass();
            Object bean = IocUtil.getBean(aClass);
            //检查下方法参数
            Type[] parameterTypes = method.getGenericParameterTypes();
            Object res;
            if (parameterTypes.length > 0) {
                Object[] objects = new Object[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    //构建方法参数
                    if (parameterTypes[i].getTypeName().contains("com.hserver.core.server.context.Request")) {
                        objects[i] = webContext.getRequest();
                    } else if (parameterTypes[i].getTypeName().contains("com.hserver.core.server.context.Response")) {
                        objects[i] = webContext.getResponse();
                    } else {
                        objects[i] = null;
                    }
                }

                res = method.invoke(bean, objects);
            } else {
                res = method.invoke(bean);
            }

            //调用结果进行设置
            if (res == null) {
                webContext.setResult("");
            } else if (res.getClass().getName().equals("java.lang.String")) {
                webContext.setResult(res.toString());
            } else {
                //非字符串类型的将对象转换Json字符串
                webContext.setResult(JSON.toJSONString(res));
            }
            return webContext;
        } catch (Exception e) {
            String message = ExceptionUtil.getMessage(e);
            log.error(message);
            throw new BusinessException(503, "调用方法失败" + message);
        }

    }

    /**
     * 构建返回对象
     *
     * @param webContext
     * @return
     */
    public static FullHttpResponse buildResponse(WebContext webContext) {

        FullHttpResponse response;

        /**
         * 如果是文件特殊处理下,是静态文件，同时需要下载的文件
         */
        if (webContext.isStaticFile()) {
            //显示型的
            response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(webContext.getStaticFile().getByteBuf()));
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, webContext.getStaticFile().getFileHead() + ";charset=UTF-8");
        } else {
            response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(webContext.getResult().getBytes(Charset.forName("UTF-8"))));
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
        }

        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        return response;
    }

    /**
     * 构建返回对象
     *
     * @param e
     * @return
     */
    public static FullHttpResponse handleException(Throwable e) {
        BusinessException cause = (BusinessException) e.getCause();
        HttpResponseStatus httpResponseStatus = HttpResponseStatus.valueOf(cause.getHttpCode());
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                httpResponseStatus,
                Unpooled.wrappedBuffer(cause.getRespMsg().getBytes(Charset.forName("UTF-8"))));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        return response;
    }

    /**
     * 终极输出
     *
     * @param ctx
     * @param future
     * @param msg
     */
    public static void writeResponse(ChannelHandlerContext ctx, CompletableFuture<HttpRequest> future, FullHttpResponse msg) {
        ctx.writeAndFlush(msg);
        future.complete(null);
    }

}
