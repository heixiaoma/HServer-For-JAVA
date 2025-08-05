package cn.hserver.netty.web.handler.http;


import cn.hserver.mvc.constants.MimeType;
import cn.hserver.mvc.constants.WebConstConfig;
import cn.hserver.netty.web.context.DefaultCookie;
import cn.hserver.netty.web.context.HttpRequest;
import cn.hserver.netty.web.context.HttpResponse;
import cn.hserver.netty.web.context.HttpResponseFile;
import cn.hserver.netty.web.util.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


public class FileHandler {
    private static final Logger log = LoggerFactory.getLogger(FileHandler.class);

    public static void handler(ChannelHandlerContext ctx,HttpRequest request, HttpResponse httpResponse) throws Exception {
        HttpResponseFile responseFile = httpResponse.getResponseFile();
        if (responseFile.isChunked()){
            handlerChunk(ctx,request,httpResponse);
        }else if (responseFile.isSupportContinue()){
            handlerContinueFile(ctx, request, httpResponse);
        }
        else {
            handlerMemory(ctx, request, httpResponse);
        }
    }


    private static void handlerContinueFile(ChannelHandlerContext ctx,HttpRequest request, HttpResponse httpResponse) throws Exception {
        HttpResponseFile responseFile = httpResponse.getResponseFile();
        File file = responseFile.getFile();
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long fileLength = raf.length();
            HttpResponseStatus status = HttpResponseStatus.OK;
            DefaultHttpHeaders headers = new DefaultHttpHeaders();
            handlerHead(request,httpResponse,headers,responseFile.getFileName());
            headers.set(HttpHeaderNames.ACCEPT_RANGES, HttpHeaderValues.BYTES);
            String range = request.getHeaders().get(HttpHeaderNames.RANGE.toString());
            long offset = 0, length = raf.length();
            if (range != null && !range.trim().isEmpty()) {
                range = range.substring(6);
                String[] split = range.split("-");
                try {
                    offset = Long.parseLong(split[0]);
                    if (split.length > 1 && split[1] != null && !split[1].trim().isEmpty()) {
                        long end = Long.parseLong(split[1]);
                        if (end <= length && offset >= end) {
                            long endIndex = end - offset;
                            headers.set(HttpHeaderNames.CONTENT_RANGE, "bytes " + offset + "-" + endIndex + "/" + length);
                        }
                    } else {
                        headers.set(HttpHeaderNames.CONTENT_RANGE, "bytes " + offset + "-" + (length + offset - 1) + "/" + (offset + length));
                    }
                    status = HttpResponseStatus.PARTIAL_CONTENT;
                } catch (Exception e) {
                    log.warn("断点续传解析错误", e);
                }
            }
            DefaultHttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
            response.headers().set(headers);
            ctx.write(response);
            ChannelFuture sendFileFuture = ctx.writeAndFlush(new DefaultFileRegion(raf.getChannel(), offset, fileLength), ctx.newProgressivePromise());
            ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            if (!request.isKeepAlive()){
                sendFileFuture.addListener(ChannelFutureListener.CLOSE);
            }
        } catch (FileNotFoundException e) {
            throw new Exception(String.format("文件 %s 找不到", file.getPath()));
        } catch (IOException e) {
            throw new Exception(String.format("读取 文件 %s 发生异常", file.getAbsolutePath()));
        }
    }

    private static void handlerChunk(ChannelHandlerContext ctx,HttpRequest request, HttpResponse httpResponse) throws Exception {
        HttpResponseFile responseFile = httpResponse.getResponseFile();
            HttpResponseStatus status = HttpResponseStatus.OK;
            DefaultHttpHeaders headers = new DefaultHttpHeaders();
            handlerHead(request,httpResponse,headers,responseFile.getFileName());
            if (responseFile.getFile()!=null) {
                headers.set(HttpHeaderNames.CONTENT_LENGTH,String.valueOf(responseFile.getFile().length()));
            }
            headers.set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
            DefaultHttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
            response.headers().set(headers);
            ctx.write(response);
            ChannelFuture channelFuture=null;
            if (responseFile.getInputStream() != null) {
                HttpChunkedInput httpChunkedInput = new HttpChunkedInput(new ChunkedStream(responseFile.getInputStream()));
                channelFuture = ctx.writeAndFlush(httpChunkedInput, ctx.newProgressivePromise());
            }
            if (responseFile.getFile() != null) {
                channelFuture = ctx.writeAndFlush(new HttpChunkedInput(new ChunkedFile(responseFile.getFile())), ctx.newProgressivePromise());
            }
            if (!request.isKeepAlive()){
                if (channelFuture!=null) {
                    channelFuture.addListener(ChannelFutureListener.CLOSE);
                }
            }
    }

    private static void handlerMemory(ChannelHandlerContext ctx,HttpRequest request, HttpResponse httpResponse) throws Exception {
        HttpResponseFile responseFile = httpResponse.getResponseFile();
            DefaultHttpHeaders headers = new DefaultHttpHeaders();
            handlerHead(request,httpResponse,headers,responseFile.getFileName());
            FullHttpResponse response;
            if (responseFile.getInputStream() != null) {
                InputStream inputStream = responseFile.getInputStream();
                response = new DefaultFullHttpResponse(
                        HTTP_1_1,
                        HttpResponseStatus.OK,
                        Unpooled.wrappedBuffer(Objects.requireNonNull(ByteBufUtil.fileToByteBuf(inputStream))));
            }else if (responseFile.getFile() != null) {
                response = new DefaultFullHttpResponse(
                        HTTP_1_1,
                        HttpResponseStatus.OK,
                        Unpooled.wrappedBuffer(Objects.requireNonNull(ByteBufUtil.fileToByteBuf(responseFile.getFile()))));
            }else if (responseFile.getContent()!=null) {
                response = new DefaultFullHttpResponse(
                        HTTP_1_1,
                        HttpResponseStatus.OK,
                        Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(responseFile.getContent())));
            }else {
                response = new DefaultFullHttpResponse(
                        HTTP_1_1,
                        HttpResponseStatus.OK,
                        Unpooled.EMPTY_BUFFER
                );
            }
            headers.set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(headers);
            ChannelFuture channelFuture = ctx.writeAndFlush(response);
            if (!request.isKeepAlive()){
                channelFuture.addListener(ChannelFutureListener.CLOSE);
            }
    }


    private static void handlerHead(HttpRequest request, HttpResponse response,HttpHeaders httpHeaders,String fileName) throws Exception {
        httpHeaders.set(HttpHeaderNames.CONTENT_TYPE, MimeType.getFileType(fileName));
        httpHeaders.add(HttpHeaderNames.CONTENT_DISPOSITION, String.format("inline; filename=\"%s\"", URLEncoder.encode(fileName, "UTF-8")));
        if (request.isKeepAlive()){
            httpHeaders.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }else {
            httpHeaders.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        }
        response.getHeaders().forEach(httpHeaders::add);
        if (request.getInnerHttpSession() != null) {
            cn.hserver.netty.web.context.DefaultCookie defaultCookie = new DefaultCookie(WebConstConfig.SESSION_KEY, request.getInnerHttpSession().id());
            defaultCookie.setHttpOnly(true);
            response.addCookie(defaultCookie);
        }
        Set<io.netty.handler.codec.http.cookie.Cookie> cookies = response.getCookies();
        if (cookies != null) {
            List<String> encode = ServerCookieEncoder.LAX.encode(cookies);
            for (String s : encode) {
                httpHeaders.add(HttpHeaderNames.SET_COOKIE, s);
            }
        }
    }


}
