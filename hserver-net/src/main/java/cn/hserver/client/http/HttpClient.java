package cn.hserver.client.http;

import cn.hserver.client.NetClient;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;

public class HttpClient implements NetClient {
    private final String url;
    private int maxConnect = 1;
    private int connectTimeout = 3000;

    public HttpClient(String url) {
        this.url = url;
    }

    public HttpClient(String url, int maxConnect, int connectTimeout) {
        this.url = url;
        this.maxConnect = maxConnect;
        this.connectTimeout = connectTimeout;
    }

    /**
     * 同步
     *
     * @param req
     * @return
     */
    public String getAsyncString(HttpReq req) {
        return null;
    }

    /**
     * 同步
     *
     * @param req
     * @return
     */
    public byte[] getAsyncBytes(HttpReq req) {
        return ByteBufUtil.getBytes((ByteBuf) getRequest(req));
    }


    private HttpRequest getRequest(HttpReq req) {
        return new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                req.getMethod(),
                req.getUri()
        );
    }

}
