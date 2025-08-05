package cn.hserver.mvc.response;


import cn.hserver.mvc.constants.HttpResponseStatus;
import cn.hserver.mvc.sse.SSeStream;
import cn.hserver.mvc.request.Cookie;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

/**
 * @author hxm
 */
public interface Response {

    /**
     * 设置请求头
     *
     * @param key
     * @param value
     */
    void addHeader(String key, String value);

    /**
     * 下载文件 字节数组
     * @param bytes
     * @param fileName
     */
    void downloadBytes(byte[] bytes, String fileName);


    /**
     * 流的下载文件-小文件合适
     *
     * @param inputStream
     * @param fileName
     */
    void downloadStream(InputStream inputStream, String fileName);

    /**
     * 下载文件 小文件合适
     * @param file
     * @param fileName
     */
    void downloadFile(File file, String fileName);

    /**
     * 下载文件-小文件合适
     * @param file
     */
    void downloadFile(File file);


    /**
     * 设置下载文件 中等文件合适，零拷贝
     *
     * @param file
     */
    void downloadContinueFile(File file);

    /**
     * 下载文件名 中等文件合适，零拷贝
     * @param file
     * @param name
     */
    void downloadContinueFile(File file,String name);

    /**
     * 分块下载流 超大文件合适
     * @param inputStream
     * @param fileName
     */
    void downloadChunkStream(InputStream inputStream, String fileName);

    /**
     * 分块下载文件 超大文件合适
     * @param file
     * @param fileName
     */
    void downloadChunkFile(File file, String fileName);

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
    void setStatus(HttpResponseStatus httpResponseStatus);

    /**
     * 使用SSE
     * 默认超时
     */

    SSeStream getSSeStream();

    /**
     * 设置超时
     * @param retryMilliseconds
     * @return
     */
    SSeStream getSSeStream(Integer retryMilliseconds);

    /**
     * Response 是否组装得有数据？
     *
     * @return
     */
    boolean hasData();

}
