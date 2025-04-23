package cn.hserver.msg;

import java.util.Map;

public class ReqMsg extends BaseMsg {
    private String method;

    private Map<String, Object> params;


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
