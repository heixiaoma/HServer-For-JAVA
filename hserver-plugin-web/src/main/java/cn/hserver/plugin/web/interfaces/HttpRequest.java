package cn.hserver.plugin.web.interfaces;


import cn.hserver.plugin.web.context.PartFile;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.cookie.Cookie;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author hxm
 */
public interface HttpRequest {

    /**
     * 最请求设计一个存储器,写
     * @param key
     * @param value
     */
    void setAttribute(String key, Object value);

    /**
     * 请求设计一个存储器 读
     * @param key
     * @return
     */
    Object getAttribute(String key);


    /**
     * 请求设计一个存储器 删
     * @param key
     */
    void removeAttribute(String key);

    /**
     * 获取请求ID
     *
     * @return
     */
    String getRequestId();

    /**
     * 获取URI，路由作用
     *
     * @return
     */
    String getUri();

    /**
     * 获取Netty的URI 带get 参数的
     *
     * @return
     */
    String getNettyUri();

    /**
     * 请求方法类型
     *
     * @return
     */
    HttpMethod getRequestType();

    /**
     * 请求参数
     *
     * @return
     */
    Map<String, List<String>> getRequestParams();

    /**
     * 获取URL的参数
     *
     * @return
     */
    Map<String, List<String>> getUrlParams();

    /**
     * 查询一个参数
     *
     * @param name
     * @return
     */
    String query(String name);


    /**
     * 查询一个参数，重URL里查询
     *
     * @param name
     * @return
     */
    String queryUrl(String name);

    /**
     * 获取所有上传的文件
     *
     * @return
     */
    Map<String, PartFile> getMultipartFile();

    /**
     * 更具名字查询一个文件对象
     *
     * @param name
     * @return
     */
    PartFile queryFile(String name);

    /**
     * 查询一个header头的值
     *
     * @param headName
     * @return
     */
    String getHeader(String headName);

    /**
     * 获取所有的header
     *
     * @return
     */
    Map<String, String> getHeaders();

    /**
     * 获取Raw方式传来的值
     *
     * @return
     */
    String getRawData();

    /**
     * 获取用户的IP
     *
     * @return
     */
    String getIp();

    /**
     * 获取真实的IP地址，有可能被代理之类的都获取
     *
     * @return
     */
    String getIpAddress();

    /**
     * 用户建立的端口
     *
     * @return
     */
    int getPort();

    /**
     * Netty ctx 对象
     *
     * @return
     */
    ChannelHandlerContext getCtx();

    /**
     * 获取Http的Body体
     *
     * @return
     */
    byte[] getBody();


    /**
     * 获取创建请求的时间
     *
     * @return
     */
    long getCreateTime();


    Set<Cookie> getCookies();
}
