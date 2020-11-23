package test8;

import io.netty.handler.codec.http.HttpHeaders;

public class HResp implements HResponse {

    private byte[] data;
    private HttpHeaders headers;
    private Throwable e;
    private int statusCode;

    public HResp(byte[] data, HttpHeaders headers, Throwable e, int statusCode) {
        this.data = data;
        this.headers = headers;
        this.e = e;
        this.statusCode = statusCode;
    }

    @Override
    public int getStatusCode() {
        return this.statusCode;
    }

    @Override
    public HttpHeaders getHeader() {
        return this.headers;
    }

    @Override
    public byte[] getBody() {
        return this.data;
    }

    @Override
    public String getBodyAsString() {
        return new String(getBody());
    }

    @Override
    public Throwable getException() {
        return this.e;
    }
}
