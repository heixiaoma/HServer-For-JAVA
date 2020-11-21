package test8;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HReq implements HRequest {

    private HConnection hConnection;

    private HttpMethod method;

    private Map<String, String> cookies = new HashMap<>();

    private Map<String, String> headers = new HashMap<>();

    private Map<String, String> formData = new HashMap<>();

    private String uri;

    private String body;

    public HReq(HConnection hConnection) {
        this.hConnection = hConnection;
    }

    @Override
    public HRequest uri(String uri) {
        this.uri = uri;
        return this;
    }

    @Override
    public HRequest cookies(Map<String, String> cookies) {
        this.cookies.putAll(cookies);
        return this;
    }

    @Override
    public HRequest cookie(String key, String value) {
        this.cookies.put(key, value);
        return this;
    }

    @Override
    public HRequest headers(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    @Override
    public HRequest header(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    @Override
    public HRequest requestBody(String body) {
        this.body=body;
        return this;
    }

    @Override
    public HRequest data(String key, String filename, InputStream inputStream) {
        return null;
    }

    @Override
    public HRequest data(String key, String value) {
        this.formData.put(key, value);
        return this;
    }

    @Override
    public HRequest data(Map<String, String> data) {
        this.formData.putAll(data);
        return this;
    }

    @Override
    public HRequest httpMethod(HttpMethod method) {
        this.method = method;
        return this;
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
