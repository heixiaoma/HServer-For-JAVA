package com.hserver.core.server.exception;

/**
 * 业务异常
 */
public class BusinessException extends RuntimeException {

    private Integer httpCode;
    private String msg;

    public BusinessException() {
        super();
    }

    public BusinessException(Integer httpCode, String msg) {
        super();
        this.httpCode = httpCode;
        this.msg = msg;
    }

    public BusinessException(String s) {
        super(s);
    }


    public String getRespMsg() {
        if (httpCode != null) {
            switch (httpCode) {
                case 404:
                    return "404：" + msg;
                case 503:
                    return "503：" + msg;
                default:
                    return "503：" + msg;
            }
        } else {
            return "未知错误";
        }
    }

    public Integer getHttpCode() {
        if (httpCode != null) {
            return httpCode;
        } else {
            return 503;
        }
    }

    public String getMsg() {
        return msg;
    }
}
