package test8;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.CookieEncoder;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HReq implements HRequest {

    private static final String DefaultUploadType = "application/octet-stream";

    private HConnection hConnection;

    private HttpMethod method;

    private Map<String, String> cookies = new HashMap<>();

    private HttpHeaders headers = new DefaultHttpHeaders();

    private Map<String, String> formData = new HashMap<>();

    private List<HFile> files = new ArrayList<>();

    private String uri;

    private String body;

    public HReq(HConnection hConnection) {
        this.hConnection = hConnection;
        //默认参数设计
        headers.add("Host", "127.0.0.1");
        headers.add("Connection", "keep-alive");
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
        headers.forEach((k, v) -> {
            this.headers.add(k, v);
        });
        return this;
    }

    @Override
    public HRequest header(String key, String value) {
        this.headers.add(key, value);
        return this;
    }

    @Override
    public HRequest requestBody(String body) {
        this.body = body;
        return this;
    }

    @Override
    public HRequest data(String key, String filename, File file) {
        this.files.add(new HFile(key, filename, file));
        return this;
    }

    @Override
    public HRequest data(String key, String filename, File file, String contentType) {
        this.files.add(new HFile(key, filename, file, contentType));
        return this;
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
        this.hConnection.write(buildRequest());
    }

    @Override
    public HResponse exec() {
        return null;
    }


    private DefaultFullHttpRequest buildRequest() {
        if (method == null) {
            throw new RuntimeException("未设置请求类型");
        }
        if (uri == null) {
            throw new RuntimeException("未设置请求URI");
        }
        DefaultFullHttpRequest defaultFullHttpRequest;
        if (this.body != null) {
            defaultFullHttpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method, uri, Unpooled.wrappedBuffer(body.getBytes(StandardCharsets.UTF_8)));
        } else {
            defaultFullHttpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method, uri);
        }
        try {
            HttpPostRequestEncoder httpPostRequestEncoder = new HttpPostRequestEncoder(defaultFullHttpRequest, true);
            formData.forEach((k, v) -> {
                try {
                    httpPostRequestEncoder.addBodyAttribute(k, v);
                } catch (HttpPostRequestEncoder.ErrorDataEncoderException e) {
                    e.printStackTrace();
                }
            });
            //文件上传类的。
            files.forEach(h -> {
                try {
                    String fileName = h.getFileName();
                    if (fileName != null) {
                        httpPostRequestEncoder.addBodyFileUpload(h.getKey(), h.getFileName(), h.getFile(), h.getContentType() == null ? DefaultUploadType : h.getContentType(), false);
                    } else {
                        httpPostRequestEncoder.addBodyFileUpload(h.getKey(), h.getFile(), h.getContentType() == null ? DefaultUploadType : h.getContentType(), false);
                    }
                } catch (HttpPostRequestEncoder.ErrorDataEncoderException e) {
                    e.printStackTrace();
                }
            });
             httpPostRequestEncoder.finalizeRequest();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        String cookie = buildCookie();
        if (cookie != null) {
            headers.add("cookie", cookie);
        }
        defaultFullHttpRequest.headers().add(headers);
        return defaultFullHttpRequest;
    }

    private String buildCookie() {
        if (cookies.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            cookies.forEach((k, v) -> {
                stringBuilder.append(k).append("=").append(v).append(";");
            });
            if (stringBuilder.toString().trim().length() == 0) {
                return null;
            }
            return stringBuilder.toString();
        }
        return null;
    }

}
