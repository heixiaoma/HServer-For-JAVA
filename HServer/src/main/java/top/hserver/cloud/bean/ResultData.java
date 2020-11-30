package top.hserver.cloud.bean;

import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.Serializable;

/**
 * @author hxm
 */
public class ResultData implements Serializable {

    private static final long SerialVersionUID = 1L;

    private String requestId;

    private HttpResponseStatus code;

    private Object data;

    private Throwable error;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public HttpResponseStatus getCode() {
        return code;
    }

    public void setCode(HttpResponseStatus code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
