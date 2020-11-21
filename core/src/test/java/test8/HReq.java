package test8;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;

import java.io.InputStream;
import java.util.Map;

public class HReq implements HRequest {

    private HConnection hConnection;

    public HReq(HConnection hConnection) {
        this.hConnection = hConnection;
    }

    @Override
    public HRequest uri(String uri) {
        return null;
    }

    @Override
    public HRequest cookies(Map<String, String> cookies) {
        return null;
    }

    @Override
    public HRequest cookie(String key, String value) {
        return null;
    }

    @Override
    public HRequest headers(Map<String, String> headers) {
        return null;
    }

    @Override
    public HRequest header(String key, String value) {
        return null;
    }

    @Override
    public HRequest requestBody(String body) {
        return null;
    }

    @Override
    public HRequest data(String key, String filename, InputStream inputStream, String contentType) {
        return null;
    }

    @Override
    public HRequest data(String key, String value) {
        return null;
    }

    @Override
    public HRequest data(Map<String, String> data) {
        return null;
    }

    @Override
    public HRequest httpMethod(HttpMethod method) {
        return null;
    }

    @Override
    public void exec(HResponse.Listener listener) {

    }

    @Override
    public HResponse exec() {
//        FullHttpRequest fullHttpRequest = new DefaultFullHttpRequest();
//        this.hConnection.write(fullHttpRequest);
        return null;
    }
}
