package top.hserver.core.server.handlers;

import com.alibaba.fastjson.JSON;
import top.hserver.core.interfaces.GlobalException;
import top.hserver.core.ioc.IocUtil;
import top.hserver.core.server.context.*;
import top.hserver.core.server.exception.BusinessException;
import top.hserver.core.server.filter.FilterChain;
import top.hserver.core.server.router.RouterInfo;
import top.hserver.core.server.router.RouterManager;
import top.hserver.core.server.util.DownLoadUtil;
import top.hserver.core.server.util.ParameterUtil;
import top.hserver.core.server.util.ExceptionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * 分发器
 */
@Slf4j
public class DispatcherHandler {

    private final static StatisticsHandler statisticsHandler = new StatisticsHandler();
    private final static StaticHandler staticHandler = new StaticHandler();
    private static final HttpDataFactory HTTP_DATA_FACTORY = new DefaultHttpDataFactory(true);
    //标识不是静态文件，这样下次使用方便直接跳过检查
    private final static CopyOnWriteArraySet<String> noStaticFileUri = new CopyOnWriteArraySet<>();

    public static WebContext buildWebContext(ChannelHandlerContext ctx,
                                             WebContext webContext) {

        HttpRequest req = webContext.getHttpRequest();
        Request request = new Request();
        request.setIp(statisticsHandler.getClientIp(ctx));
        webContext.setCtx(ctx);
        //如果GET请求
        if (req.method() == GET) {
            Map<String, String> requestParams = new HashMap<>();
            QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
            Map<String, List<String>> parame = decoder.parameters();
            Iterator<Map.Entry<String, List<String>>> iterator = parame.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, List<String>> next = iterator.next();
                requestParams.put(next.getKey(), next.getValue().get(0));
            }
            request.setRequestParams(requestParams);
        }

        //如果POST请求
        if (req.method() == POST) {
            //檢查是否有文件上传
            try {
                HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(HTTP_DATA_FACTORY, req);
                boolean isMultipart = decoder.isMultipart();
                List<ByteBuf> byteBuffs = new ArrayList<>(webContext.getContents().size());
                for (HttpContent content : webContext.getContents()) {
                    if (!isMultipart) {
                        byteBuffs.add(content.content().copy());
                    }
                    decoder.offer(content);
                    request.readHttpDataChunkByChunk(decoder);
                    content.release();
                }
                if (!byteBuffs.isEmpty()) {
                    request.setBody(Unpooled.copiedBuffer(byteBuffs.toArray(new ByteBuf[0])));
                }
            } catch (Exception e) {
                GlobalException bean2 = IocUtil.getBean(GlobalException.class);
                if (bean2 != null) {
                    bean2.handler(e, webContext.getWebkit());
                } else {
                    String message = ExceptionUtil.getMessage(e);
                    log.error(message);
                    throw new BusinessException(503, "生成解码器失败" + message);
                }
            }
        }
        //获取URi，設置真實的URI
        int i = req.uri().indexOf("?");
        if (i > 0) {
            String uri = req.uri();
            request.setUri(uri.substring(0, i));
        } else {
            request.setUri(req.uri());
        }
        request.setRequestType(req.method());

        //处理Headers
        Map<String, String> headers = new ConcurrentHashMap<>();
        req.headers().names().forEach(a -> headers.put(a, req.headers().get(a)));
        request.setHeaders(headers);
        webContext.setRequest(request);
        webContext.setResponse(new Response());
        Webkit webkit = new Webkit();
        webkit.httpRequest = webContext.getRequest();
        webkit.httpResponse = webContext.getResponse();
        webContext.setWebkit(webkit);
        return webContext;
    }

    /**
     * 统计
     *
     * @param webContext
     * @return
     */
    public static WebContext Statistics(WebContext webContext) {
        if (ConstConfig.isStatisticsOpen) {

            for (String statisticalRule : ConstConfig.StatisticalRules) {
                if (webContext.getRequest().getUri().matches(statisticalRule)) {
                    long startTime = System.currentTimeMillis();
                    //统计方法调用时长
                    webContext.regStatistics((stopTime) -> {
                        //URI访问数
                        statisticsHandler.uriDataCount(webContext.getRequest().getUri());
                        //请求总数统计
                        statisticsHandler.increaseCount();
                        //統計IP
                        statisticsHandler.addToIpMap(webContext.getCtx());
                        //计算统计请求的，数据包大小,IP,执行时间
                        statisticsHandler.addToConnectionDeque(webContext.getCtx(), webContext.getRequest().getUri(), stopTime - startTime);
                    });
                    break;
                }
            }
        }
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
        StaticFile handler = staticHandler.handler(webContext.getRequest().getUri(),webContext);
        if (handler != null) {
            webContext.setStaticFile(true);
            webContext.setStaticFile(handler);
        } else {
            noStaticFileUri.add(webContext.getRequest().getUri());
        }
        return webContext;
    }

    public static WebContext filter(WebContext webContext) {
        /**
         * 检测下Filter的过滤哈哈
         */
        if (!FilterChain.filtersIoc.isEmpty()) {
            //不是空就要进行Filter过滤洛
            webContext.setFilter(true);

            try {
                FilterChain.getFileChain().doFilter(webContext.getWebkit());
            }catch (Exception e){
                GlobalException bean2 = IocUtil.getBean(GlobalException.class);
                if (bean2 != null) {
                    bean2.handler(e, webContext.getWebkit());
                    return webContext;
                } else {
                    String message = ExceptionUtil.getMessage(e);
                    log.error(message);
                    throw new BusinessException(503, "拦截器异常" + message);
                }
            }
            //Filter走完又回来
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

        RouterInfo routerInfo = RouterManager.getRouterInfo(webContext.getRequest().getUri(), webContext.getRequest().getRequestType());
        if (routerInfo == null) {
            GlobalException bean1 = IocUtil.getBean(GlobalException.class);
            if (bean1 != null) {
                bean1.handler(new NullPointerException("为找到对应的控制器"), webContext.getWebkit());
                return webContext;
            } else {
                log.error("为找到对应的控制器");
                throw new BusinessException(404, "为找到对应的控制器");
            }
        }
        try {
            Method method = routerInfo.getMethod();
            Class<?> aClass = routerInfo.getaClass();
            Object bean = IocUtil.getBean(aClass);
            //检查下方法参数
            Object res;
            //如果控制器有参数，那么就进行，模糊赋值，在检测是否有req 和resp
            try {

                Object[] methodArgs = null;
                try {
                    methodArgs = ParameterUtil.getMethodArgs(aClass, method, webContext);
                } catch (Exception e) {
                    GlobalException bean2 = IocUtil.getBean(GlobalException.class);
                    if (bean2 != null) {
                        bean2.handler(e, webContext.getWebkit());
                        return webContext;
                    } else {
                        String message = ExceptionUtil.getMessage(e);
                        log.error(message);
                        throw new BusinessException(503, "生成控制器时参数异常" + message);
                    }
                }
                if (methodArgs != null) {
                    res = method.invoke(bean, methodArgs);
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
                    webContext.getResponse().setHeader("content-type", "application/json;charset=UTF-8");
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                GlobalException bean2 = IocUtil.getBean(GlobalException.class);
                if (bean2 != null) {
                    bean2.handler(e, webContext.getWebkit());
                    return webContext;
                } else {
                    String message = ExceptionUtil.getMessage(e);
                    log.error(message);
                    throw new BusinessException(503, "调用方法失败" + message);
                }
            } catch (IllegalArgumentException e) {
                GlobalException bean1 = IocUtil.getBean(GlobalException.class);
                if (bean1 != null) {
                    bean1.handler(e, webContext.getWebkit());
                    return webContext;
                } else {
                    String message = ExceptionUtil.getMessage(e);
                    log.error(message);
                    throw new BusinessException(503, "调用控制器时参数异常" + message);
                }
            }
            return webContext;
        } catch (BusinessException e) {
            GlobalException bean1 = IocUtil.getBean(GlobalException.class);
            if (bean1 != null) {
                bean1.handler(e, webContext.getWebkit());
            } else {
                String message = ExceptionUtil.getMessage(e);
                log.error(message);
                throw new BusinessException(e.getHttpCode(), e.getMsg() + message);
            }
        }
        return webContext;
    }

        /**
         * 构建返回对象
         *
         * @param webContext
         * @return
         */
        public static FullHttpResponse buildResponse (WebContext webContext){
            try {
                FullHttpResponse response;
                /**
                 * 如果是文件特殊处理下,是静态文件，同时需要下载的文件
                 */
                if (webContext.isStaticFile()) {
                    //显示型的
                    response = new DefaultFullHttpResponse(
                            HTTP_1_1,
                            HttpResponseStatus.OK,
                            Unpooled.wrappedBuffer(webContext.getStaticFile().getByteBuf()));
                    response.headers().set(HttpHeaderNames.CONTENT_TYPE, webContext.getStaticFile().getFileHead() + ";charset=UTF-8");
                } else if (webContext.getResponse().isDownload()) {
                    //控制器下载文件的
                    Response response1 = webContext.getResponse();

                    if (response1.getFile() == null) {
                        response = new DefaultFullHttpResponse(
                                HTTP_1_1,
                                HttpResponseStatus.OK,
                                Unpooled.wrappedBuffer(DownLoadUtil.FileToByteBuf(response1.getInputStream())));
                    } else {
                        response = new DefaultFullHttpResponse(
                                HTTP_1_1,
                                HttpResponseStatus.OK,
                                Unpooled.wrappedBuffer(DownLoadUtil.FileToByteBuf(response1.getFile())));
                    }
                    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream;charset=UTF-8");
                    response.headers().add(HttpHeaderNames.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", webContext.getResponse().getFileName()));
                } else if (webContext.getResponse().getJsonAndHtml() != null) {
                    //是否Response的
                    response = new DefaultFullHttpResponse(
                            HTTP_1_1,
                            HttpResponseStatus.OK,
                            Unpooled.wrappedBuffer(webContext.getResponse().getJsonAndHtml().getBytes(Charset.forName("UTF-8"))));
                } else {
                    //是否是方法调用的
                    response = new DefaultFullHttpResponse(
                            HTTP_1_1,
                            HttpResponseStatus.OK,
                            Unpooled.wrappedBuffer(webContext.getResult().getBytes(Charset.forName("UTF-8"))));
                    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
                }
                response.headers().set(HttpHeaderNames.SERVER, "HServer");
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
            } catch (Exception e) {
                String message = ExceptionUtil.getMessage(e);
                log.error(message);
                throw new BusinessException(503, "构建Response对象异常" + message);
            }
        }

        /**
         * 构建返回对象
         *
         * @param e
         * @return
         */
        public static FullHttpResponse handleException (Throwable e){
            BusinessException cause = (BusinessException) e.getCause();
            HttpResponseStatus httpResponseStatus = HttpResponseStatus.valueOf(cause.getHttpCode());
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HTTP_1_1,
                    httpResponseStatus,
                    Unpooled.wrappedBuffer(cause.getRespMsg().getBytes(Charset.forName("UTF-8"))));
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
            response.headers().set(HttpHeaderNames.SERVER, "HServer");
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
        public static void writeResponse (ChannelHandlerContext
        ctx, CompletableFuture < WebContext > future, FullHttpResponse msg){
            ctx.writeAndFlush(msg);
            future.complete(null);
        }

    }
