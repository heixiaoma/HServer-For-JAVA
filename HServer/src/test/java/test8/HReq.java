package test8;

import io.netty.handler.codec.http.*;
import okhttp3.*;
import top.hserver.core.server.context.HeadMap;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HReq implements HRequest {

    private static OkHttpClient okHttpClient = new OkHttpClient();

    private final String contentType = "content-type";

    private HttpMethod method;

    private Map<String, String> cookies = new HashMap<>();

    private Map<String, String> headers = new HashMap<>();

    private Map<String, String> formData = new HashMap<>();

    private List<HFile> files = new ArrayList<>();

    private String uri;

    private String body;

    private String baseAddress;

    public HReq(String baseAddress) {
        this.baseAddress = baseAddress;
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
    public HRequest requestBody(String body, String contentType) {
        this.body = body;
        this.headers.put(this.contentType, contentType);
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
        okHttpClient.newCall(buildRequest()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.exception(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                HResp hResp = new HResp(
                        response.body().bytes(),
                        buildHeader(response.headers()),
                        null,
                        response.code()
                );
                listener.complete(hResp);
            }
        });
    }

    @Override
    public HResponse exec() {
        try {
            Response response = okHttpClient.newCall(buildRequest()).execute();
            return new HResp(
                    response.body().bytes(),
                    buildHeader(response.headers()),
                    null,
                    response.code());

        } catch (IOException e) {
            return new HResp(
                    null,
                    null,
                    e,
                    -1);
        }
    }

    private RequestBody buildBody() {
        if (HttpMethod.GET == this.method) {
            return null;
        }
        RequestBody body = null;
        if (formData.size() > 0 && files.size() == 0) {
            FormBody.Builder builder = new FormBody.Builder();
            formData.forEach(builder::add);
            body = builder.build();
        } else if (files.size() > 0) {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            formData.forEach(builder::addFormDataPart);
            for (HFile file : files) {
                builder.addFormDataPart(
                        file.getKey(),
                        file.getFileName(),
                        RequestBody.create(MediaType.parse(file.getContentType()), file.getFile())
                );
            }
            body = builder.build();
        } else if (this.body != null) {
            body = RequestBody.create(MediaType.parse(headers.get(contentType)), this.body);
        } else {
            body = new FormBody.Builder().build();
        }
        return body;
    }

    private HeadMap buildHeader(Headers headers) {
        Set<String> names = headers.names();
        HeadMap headMap = new HeadMap();
        for (String name : names) {
            headMap.put(name, headers.get(name));
        }
        return headMap;
    }


    private String buildCookie() {
        if (cookies.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            cookies.forEach((k, v) -> stringBuilder.append(k).append("=").append(v).append(";"));
            if (stringBuilder.toString().trim().length() == 0) {
                return null;
            }
            return stringBuilder.toString();
        }
        return null;
    }

    private Request buildRequest() {
        Request.Builder builder = new Request.Builder();
        builder.url(baseAddress + uri);
        headers.forEach(builder::header);
        String s = buildCookie();
        if (s != null) {
            builder.header("Cookie", s);
        }
        return builder
                .method(this.method.name(), buildBody())
                .build();
    }

}
