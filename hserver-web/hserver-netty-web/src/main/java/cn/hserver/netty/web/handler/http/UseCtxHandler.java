package cn.hserver.netty.web.handler.http;


import cn.hserver.mvc.constants.MimeType;
import cn.hserver.mvc.response.ProgressStatus;
import cn.hserver.netty.web.context.HttpRequest;
import cn.hserver.netty.web.context.HttpResponse;
import cn.hserver.netty.web.context.HttpResponseFile;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URLEncoder;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


public class UseCtxHandler {
    private static final Logger log = LoggerFactory.getLogger(UseCtxHandler.class);

    private static void bigFile(ChannelHandlerContext ctx,HttpRequest request, HttpResponse httpResponse) throws Exception {
        HttpResponseFile responseFile = httpResponse.getResponseFile();
        File file = responseFile.getFile();
        ProgressStatus progressStatus = responseFile.getProgressStatus();
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            long fileLength = raf.length();
            HttpResponseStatus status = HttpResponseStatus.OK;
            DefaultHttpHeaders headers = new DefaultHttpHeaders();
            httpResponse.getHeaders().forEach(headers::add);
            headers.set(HttpHeaderNames.ACCEPT_RANGES, HttpHeaderValues.BYTES);
            headers.set(HttpHeaderNames.CONTENT_LENGTH, fileLength);
            headers.set(HttpHeaderNames.CONTENT_TYPE, MimeType.getFileType(file.getName()));
            headers.add(HttpHeaderNames.CONTENT_DISPOSITION, String.format("inline; filename=\"%s\"", URLEncoder.encode(file.getName(), "UTF-8")));
            String range = request.getHeaders().get(HttpHeaderNames.RANGE.toString());
            long offset, length = raf.length();
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
                            length = endIndex - offset;
                        }
                    } else {
                        headers.set(HttpHeaderNames.CONTENT_RANGE, "bytes " + offset + "-" + (length + offset - 1) + "/" + (offset + length));
                        length = length - offset;
                    }
                    headers.set(HttpHeaderNames.CONTENT_LENGTH, length);// 重写响应长度
                    status = HttpResponseStatus.PARTIAL_CONTENT;
                } catch (Exception e) {
                    log.warn("断点续传解析错误", e);
                }
            }
            DefaultHttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
            response.headers().set(headers);
            ctx.write(response);
            ChannelFuture sendFileFuture = ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());
            sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
                @Override
                public void operationComplete(ChannelProgressiveFuture future)
                        throws Exception {
                    log.debug("文件 {} 下载完成.", file.getName());
                    raf.close();
                    if (progressStatus!=null) {
                        progressStatus.operationComplete(file.getAbsolutePath());
                    }
                }

                @Override
                public void operationProgressed(ChannelProgressiveFuture future,
                                                long progress, long total) throws Exception {
                    if (progressStatus!=null) {
                        progressStatus.downloading(progress, total);
                    }
                }
            });
            ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        } catch (FileNotFoundException e) {
            throw new Exception(String.format("文件 %s 找不到", file.getPath()));
        } catch (IOException e) {
            throw new Exception(String.format("读取 文件 %s 发生异常", file.getAbsolutePath()));
        }
    }

    public static void useCtx(ChannelHandlerContext ctx,HttpRequest request,HttpResponse response) throws Exception{
        HttpResponseFile responseFile = response.getResponseFile();
        if (responseFile!=null&& responseFile.isBigFile()){
            bigFile(ctx,request,response);
        }
    }
}
