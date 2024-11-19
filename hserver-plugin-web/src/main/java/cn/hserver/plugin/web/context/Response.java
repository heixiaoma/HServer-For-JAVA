package cn.hserver.plugin.web.context;

import cn.hserver.plugin.web.util.FreemarkerUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.hserver.plugin.web.interfaces.HttpResponse;
import cn.hserver.plugin.web.interfaces.ProgressStatus;

import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.RandomAccess;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author hxm
 */
public class Response implements HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(Response.class);

    private final HeadMap headers = new HeadMap();

    private File file;

    private InputStream inputStream;

    private boolean isDownload = false;

    private String fileName;

    private String result = null;

    private HttpResponseStatus httpResponseStatus;

    private boolean useCtx = false;


    @Override
    public boolean hasData() {
        //重定向，json html 等
        if (result != null) {
            return true;
        }
        //下载文件
        if (isDownload) {
            return true;
        }
        return false;
    }

    /**
     * 设置响应头
     *
     * @param key
     * @param value
     */
    @Override
    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    /**
     * 下载文件
     *
     * @param file
     */
    @Override
    public void setDownloadFile(File file) {
        this.file = file;
        this.isDownload = true;
        this.fileName = file.getName();
    }

    @Override
    public void setDownloadBigFile(File file) throws Exception {
        this.setDownloadBigFile(file,null);
    }

    /**
     * 下载大文件
     *
     * @param file
     */
    @Override
    public void setDownloadBigFile(File file, ProgressStatus progressStatus) throws Exception {
        useCtx = true;
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            Webkit webKit = HServerContextHolder.getWebKit();
            long fileLength = raf.length();
            HttpResponseStatus status = HttpResponseStatus.OK;
            DefaultHttpHeaders headers = new DefaultHttpHeaders();
            headers.set(HttpHeaderNames.ACCEPT_RANGES, HttpHeaderValues.BYTES);
            headers.set(HttpHeaderNames.CONTENT_LENGTH, fileLength);
            headers.set(HttpHeaderNames.CONTENT_TYPE, MimeType.getFileType(file.getName()));
            headers.add(HttpHeaderNames.CONTENT_DISPOSITION, String.format("inline; filename=\"%s\"", URLEncoder.encode(file.getName(), "UTF-8")));
            String range = webKit.httpRequest.getHeaders().get(HttpHeaderNames.RANGE.toString());
            long offset, length = raf.length();
            if (range != null && range.trim().length() != 0) {
                range = range.substring(6);
                String[] split = range.split("-");
                try {
                    offset = Long.parseLong(split[0]);
                    if (split.length > 1 && split[1] != null && split[1].trim().length() != 0) {
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
            webKit.httpRequest.getCtx().write(response);
            ChannelFuture sendFileFuture = webKit.httpRequest.getCtx().write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), webKit.httpRequest.getCtx().newProgressivePromise());
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
            webKit.httpRequest.getCtx().writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        } catch (FileNotFoundException e) {
            throw new Exception(String.format("文件 %s 找不到", file.getPath()));
        } catch (IOException e) {
            throw new Exception(String.format("读取 文件 %s 发生异常", file.getAbsolutePath()));
        }
    }

    /**
     * 下载文件啦
     *
     * @param inputStream
     */
    @Override
    public void setDownloadFile(InputStream inputStream, String fileName) {
        this.inputStream = inputStream;
        this.isDownload = true;
        this.fileName = fileName;
    }


    @Override
    public void sendJsonString(String jsonStr) {
        this.result = jsonStr;
        if (!headers.containsKey("content-type")) {
            headers.put("content-type", "application/json;charset=UTF-8");
        }
    }

    @Override
    public void sendJson(Object object) {
        try {
            this.result = WebConstConfig.JSONADAPTER.convertString(object);
            if (!headers.containsKey("content-type")) {
                headers.put("content-type", "application/json;charset=UTF-8");
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }

    @Override
    public void sendHtml(String html) {
        this.result = html;
        if (!headers.containsKey("content-type")) {
            headers.put("content-type", "text/html;charset=UTF-8");
        }
    }

    @Override
    public void sendText(String text) {
        this.result = text;
        if (!headers.containsKey("content-type")) {
            headers.put("content-type", "text/plain;charset=UTF-8");
        }
    }

    /**
     * 设置一个空字符
     */
    public void sendNull() {
        this.result = "";
    }

    @Override
    public void sendStatusCode(HttpResponseStatus httpResponseStatus) {
        this.httpResponseStatus = httpResponseStatus;
    }

    @Override
    public void setUseCtx(boolean p) {
        this.useCtx = p;
    }

    @Override
    public void sendTemplate(String htmlPath, Map<String, Object> obj) {
        try {
            this.result = FreemarkerUtil.getTemplate(htmlPath, obj);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        if (!headers.containsKey("content-type")) {
            headers.put("content-type", "text/html;charset=UTF-8");
        }
    }

    @Override
    public void sendTemplate(String htmlPath) {
        this.sendTemplate(htmlPath, new HashMap<>(0));
    }

    /**
     * 添加Cookie
     *
     * @param cookie
     */
    @Override
    public void addCookie(Cookie cookie) {
        String encode = ServerCookieEncoder.LAX.encode(cookie);
        headers.put(String.valueOf(HttpHeaderNames.SET_COOKIE), encode);
    }

    @Override
    public void redirect(String url) {
        this.result = "";
        headers.put("location", url);
    }


    //---------------系统用的Get操作

    public Map<String, String> getHeaders() {
        return headers;
    }

    public File getFile() {
        return file;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public String getFileName() {
        return fileName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public HttpResponseStatus getHttpResponseStatus() {
        return httpResponseStatus;
    }

    public boolean isUseCtx() {
        return useCtx;
    }

}
