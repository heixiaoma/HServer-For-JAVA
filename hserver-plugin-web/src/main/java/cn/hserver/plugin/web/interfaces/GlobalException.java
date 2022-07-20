package cn.hserver.plugin.web.interfaces;


import cn.hserver.plugin.web.context.Webkit;

/**
 * @author hxm
 */
public interface GlobalException {

    /**
     * 错误
     * 错误状态码
     * 错误描述
     * req和resp 对象
     * @param throwable
     * @param httpStatusCode
     * @param errorDescription
     * @param webkit
     */
    void handler(Throwable throwable, int httpStatusCode, String errorDescription, Webkit webkit);
}
