package cn.hserver.mvc.exception;

import cn.hserver.mvc.constants.HttpResponseStatus;
import cn.hserver.mvc.context.WebContext;

/**
 * 业务异常
 * @author hxm
 */
public class WebException extends RuntimeException {

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
    private WebContext webContext;
    /**
     * 真实的报错
     */
    private Throwable throwable;

    public WebException() {
        super();
    }

    public WebException(Integer httpCode, String errorDescription, Throwable throwable, WebContext webContext) {
        super();
        this.httpCode = httpCode;
        this.errorDescription = errorDescription;
        this.webContext=webContext;
        this.throwable=throwable;
    }

    public WebException(String s) {
        super(s);
    }

    public Integer getHttpCode() {
        if (httpCode != null) {
            return httpCode;
        } else {
            return HttpResponseStatus.INTERNAL_SERVER_ERROR.getCode();
        }
    }


    public String getErrorDescription() {
        return errorDescription;
    }

    public WebContext getWebContext() {
        return webContext;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
