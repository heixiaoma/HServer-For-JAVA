package test8;

import io.netty.handler.codec.http.HttpMethod;

import java.io.File;
import java.util.Map;

public interface HRequest {

    HRequest uri(String uri);

    HRequest cookies(Map<String, String> cookies);

    HRequest cookie(String key, String value);

    HRequest headers(Map<String, String> headers);

    HRequest header(String key, String value);

    HRequest requestBody(String body);

    HRequest data(String key, String filename, File file);

    HRequest data(String key, String filename, File file,String contentType);

    HRequest data(String key, String value);

    HRequest data(Map<String, String> data);

    HRequest httpMethod(HttpMethod method);

    HResponse exec();

    void exec(HResponse.Listener listener);

}
