package cn.hserver.netty.web.context;

import cn.hserver.mvc.constants.WebConstConfig;
import cn.hserver.mvc.request.HeadMap;
import cn.hserver.mvc.response.Response;
import cn.hserver.mvc.sse.SSeStream;
import cn.hserver.netty.web.handler.http.NettSse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class HttpResponse implements Response {

    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    //header
    private final HeadMap headers = new HeadMap();
    //Cookie
    private Set<Cookie> cookies;
    //文件响应
    private HttpResponseFile responseFile;
    //字符串响应内容
    private String result = null;
    //状态码
    private HttpResponseStatus httpResponseStatus;
    //是否keepLive
    private boolean useCtx = false;

    private ChannelHandlerContext ctx;


    @Override
    public boolean hasData() {
        //重定向，json html 等
        if (result != null) {
            return true;
        }
        //下载文件
        if (responseFile!=null) {
            return true;
        }
        //stream使用
        if(useCtx){
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
    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    @Override
    public void downloadBytes(byte[] bytes, String fileName) {
        if (bytes==null){
            throw new RuntimeException("下载字节数组不能为空");
        }
        this.responseFile=new HttpResponseFile(bytes, null,null, fileName,false,false);
    }


    @Override
    public void downloadFile(File file) {
        if (!file.isFile()){
            throw new RuntimeException("这不是一个文件");
        }
        this.responseFile=new HttpResponseFile(null,file, null, file.getName(),false,false);
    }
    @Override
    public void downloadFile(File file, String name) {
        if (!file.isFile()){
            throw new RuntimeException("这不是一个文件");
        }
        this.responseFile=new HttpResponseFile(null,file, null,name,false,false);
    }

    @Override
    public void downloadStream(InputStream inputStream, String fileName) {
        if (inputStream!=null){
            throw new RuntimeException("stream不能为空");
        }
        this.responseFile=new HttpResponseFile(null,null, inputStream, fileName,false,false);
    }

    @Override
    public void downloadChunkFile(File file, String fileName) {
        if (!file.isFile()){
            throw new RuntimeException("这不是一个文件");
        }
        this.responseFile=new HttpResponseFile(null,file, null, fileName,true,false);
    }

    @Override
    public void downloadChunkStream(InputStream inputStream, String fileName) {
        if (inputStream!=null){
            throw new RuntimeException("stream不能为空");
        }
        this.responseFile=new HttpResponseFile(null,null, inputStream, fileName,true,false);
    }

    @Override
    public void downloadContinueFile(File file) {
        if (!file.isFile()){
            throw new RuntimeException("这不是一个文件");
        }
        this.responseFile=new HttpResponseFile(null,file, null,file.getName(),false,true);
    }

    @Override
    public void downloadContinueFile(File file, String name) {
        if (!file.isFile()){
            throw new RuntimeException("这不是一个文件");
        }
        this.responseFile=new HttpResponseFile(null,file, null,name,false,true);
    }

    @Override
    public SSeStream getSSeStream() {
        return getSSeStream(null);
    }

    @Override
    public SSeStream getSSeStream(Integer retryMilliseconds) {
        useCtx=true;
        return new NettSse(retryMilliseconds,this);
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


    @Override
    public void setStatus(cn.hserver.mvc.constants.HttpResponseStatus httpResponseStatus) {
        this.httpResponseStatus = HttpResponseStatus.valueOf(httpResponseStatus.getCode());
    }

    @Override
    public void sendTemplate(String htmlPath, Map<String, Object> obj) {
        try {
            if (WebConstConfig.template==null){
                throw new Exception("未集成模版引擎");
            }
            this.result = WebConstConfig.template.getTemplate(htmlPath, obj);
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

    @Override
    public void addCookie(cn.hserver.mvc.request.Cookie cookie) {
        if (cookies==null){
            cookies=new HashSet<>();
        }
        io.netty.handler.codec.http.cookie.DefaultCookie defaultCookie=new io.netty.handler.codec.http.cookie.DefaultCookie(cookie.name(),cookie.value());
        defaultCookie.setDomain(cookie.domain());
        defaultCookie.setPath(cookie.path());
        defaultCookie.setMaxAge(cookie.maxAge());
        defaultCookie.setHttpOnly(cookie.isHttpOnly());
        defaultCookie.setSecure(cookie.isSecure());
        cookies.add(defaultCookie);
    }

    @Override
    public void redirect(String url) {
        this.result = "";
        headers.put("location", url);
    }

    public HttpResponseStatus getHttpResponseStatus() {
        return httpResponseStatus;
    }

    public HttpResponseFile getResponseFile() {
        return responseFile;
    }

    public void setUseCtx(boolean p) {
        this.useCtx = p;
    }

    public boolean isUseCtx() {
        return useCtx;
    }

    public boolean isFile() {
        return responseFile!=null;
    }

    public String getResult() {
        return result;
    }

    public Set<Cookie> getCookies() {
        return cookies;
    }

    public HeadMap getHeaders() {
        return headers;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }
}
