package cn.hserver.client.http;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;

public class HttpReq {

    private HttpMethod method;
    private int timeout;

    private HttpHeaders httpHeaders;

    private String uri;


    public HttpMethod getMethod() {
        return method;
    }

    public int getTimeout() {
        return timeout;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public String getUri() {
        return uri;
    }

    public HttpReq setMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    public HttpReq setTimeout(int i) {
        this.method = method;
        return this;
    }

    public HttpReq setHeader(String s, String s1) {
        if (httpHeaders == null) {
            httpHeaders = new DefaultHttpHeaders();
        }
        httpHeaders.add(s, s1);
        return this;
    }
    public HttpReq setUri(String s) {
        this.uri = s;
        return this;
    }


}
