package test8;

import top.hserver.core.server.context.HeadMap;

public class HResp implements HResponse {

    private byte[] data;
    private HeadMap headers;
    private Throwable e;
    private int statusCode;

    public HResp(byte[] data, HeadMap headers, Throwable e, int statusCode) {
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
    public HeadMap getHeader() {
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
