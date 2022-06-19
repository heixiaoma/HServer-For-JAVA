package net.hserver.core.server.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import net.hserver.core.server.context.Webkit;

/**
 * 业务异常
 * @author hxm
 */
public class BusinessException extends RuntimeException {

    /**
     * 错误状态码
     */
    private Integer httpCode;
    /**
     * 错误类型
     */
    private String errorDescription;
    /**
     * req和resp
     */
    private Webkit webkit;
    /**
     * 真实的报错
     */
    private Throwable throwable;

    public BusinessException() {
        super();
    }

    public BusinessException(Integer httpCode, String errorDescription,Throwable throwable,Webkit webkit) {
        super();
        this.httpCode = httpCode;
        this.errorDescription = errorDescription;
        this.webkit=webkit;
        this.throwable=throwable;
    }

    public BusinessException(String s) {
        super(s);
    }

    public Integer getHttpCode() {
        if (httpCode != null) {
            return httpCode;
        } else {
            return HttpResponseStatus.INTERNAL_SERVER_ERROR.code();
        }
    }


    public String getErrorDescription() {
        return errorDescription;
    }

    public Webkit getWebkit() {
        return webkit;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
