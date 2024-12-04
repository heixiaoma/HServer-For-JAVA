package cn.hserver.plugin.web.interfaces;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.cookie.Cookie;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

/**
 * @author hxm
 */
public interface HttpResponse {

    /**
     * 设置请求头
     *
     * @param key
     * @param value
     */
    void setHeader(String key, String value);

    /**
     * 设置下载文件
     *
     * @param file
     */
    void setDownloadFile(File file);


    /**
     * 下载大文件
     *
     * @param file
     * @param progressStatus
     * @throws Exception
     */
    void setDownloadBigFile(File file, ProgressStatus progressStatus) throws Exception;

    /**
     * 下载大文件
     * @param file
     * @throws Exception
     */
    void setDownloadBigFile(File file) throws Exception;

    /**
     * 流的下载文件
     *
     * @param inputStream
     * @param fileName
     */
    void setDownloadFile(InputStream inputStream, String fileName);

    /**
     * 发送Object对象自动转json
     *
     * @param object
     */
    void sendJson(Object object);

    /**
     * 发送String的字符串
     *
     * @param jsonStr
     */
    void sendJsonString(String jsonStr);

    /**
     * 发送HTML
     *
     * @param html
     */
    void sendHtml(String html);

    /**
     * 发送文本
     *
     * @param text
     */
    void sendText(String text);

    /**
     * Freemarker模板
     *
     * @param htmlPath
     * @param obj
     */
    void sendTemplate(String htmlPath, Map<String, Object> obj);


    /**
     * Freemarker模板
     *
     * @param htmlPath
     * @param
     */
    void sendTemplate(String htmlPath);


    /**
     * 添加Cookie
     *
     * @param cookie
     */
    void addCookie(Cookie cookie);

    /**
     * 重定向
     *
     * @param url
     */
    void redirect(String url);


    /**
     * 设置状态码
     *
     * @param httpResponseStatus
     */
    void sendStatusCode(HttpResponseStatus httpResponseStatus);


    /**
     * Response 是否组装得有数据？
     *
     * @return
     */
    boolean hasData();

    /**
     * 设置为代理模式，就不输出数据了
     *
     * @param p
     */
    void setUseCtx(boolean p);

}
