package cn.hserver.plugin.gateway.bean;

import io.netty.buffer.ByteBuf;

public class Http4Data {
    private String host;
    private Object data;

    public Http4Data() {
    }

    public Http4Data(String host, Object data) {
        this.host = host;
        this.data = data;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
