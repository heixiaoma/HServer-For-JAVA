package cn.hserver.msg;

import cn.hserver.plugin.web.context.WebConstConfig;

public class BaseMsg {
    private String jsonrpc;
    private Object id;

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String toJsonString() {
        try {
            return WebConstConfig.JSON.writeValueAsString(this);
        } catch (Exception e) {
            return null;
        }
    }
}
