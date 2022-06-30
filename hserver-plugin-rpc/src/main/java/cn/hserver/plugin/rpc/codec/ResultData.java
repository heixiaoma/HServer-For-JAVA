package cn.hserver.plugin.rpc.codec;

import java.io.Serializable;

public class ResultData implements Serializable {
    private static final long SerialVersionUID = 1L;
    private String requestId;
    private MsgCode code;
    private Object data;
    private Throwable error;

    public ResultData() {
    }

    public String getRequestId() {
        return this.requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }


    public MsgCode getCode() {
        return code;
    }

    public void setCode(MsgCode code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Throwable getError() {
        return this.error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
