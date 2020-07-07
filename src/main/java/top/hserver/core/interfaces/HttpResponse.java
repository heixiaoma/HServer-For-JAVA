package top.hserver.core.interfaces;

import io.netty.handler.codec.http.HttpResponseStatus;
import top.hserver.core.server.context.Cookie;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

/**
 * @author hxm
 */
public interface HttpResponse {

    /**
     * 设置请求头
     * @param key
     * @param value
     */
    void setHeader(String key, String value);

    /**
     * 设置下载文件
     * @param file
     */
    void setDownloadFile(File file);

    /**
     * 流的下载文件
     * @param inputStream
     * @param fileName
     */
    void setDownloadFile(InputStream inputStream, String fileName);

    /**
     * 发送Object对象自动转json
     * @param object
     */
    void sendJson(Object object);

    /**
     * 发送String的字符串
     * @param jsonStr
     */
    void sendJsonString(String jsonStr);

    /**
     * 发送HTML
     * @param html
     */
    void sendHtml(String html);

    /**
     * 发送文本
     * @param text
     */
    void sendText(String text);

    /**
     * Freemarker模板
     * @param htmlPath
     * @param obj
     */
    void sendTemplate(String htmlPath, Map<String, Object> obj);

    /**
     * 添加Cookie
     * @param cookie
     */
    void addCookie(Cookie cookie);

    /**
     * 重定向
     * @param url
     */
    void redirect(String url);


    /**
     * 设置状态码
     * @param httpResponseStatus
     */
    void sendStatusCode(HttpResponseStatus httpResponseStatus);

}
