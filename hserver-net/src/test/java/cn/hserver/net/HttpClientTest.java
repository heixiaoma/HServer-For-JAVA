package cn.hserver.net;

import cn.hserver.client.http.HttpClient;
import cn.hserver.client.http.HttpReq;
import io.netty.handler.codec.http.HttpMethod;

public class HttpClientTest {

    public static void main(String[] args) {
        HttpClient httpClient = new HttpClient("https://baidu.com");
        HttpReq httpReq = new HttpReq();
        httpReq.setMethod(HttpMethod.GET);
        httpReq.setTimeout(10000);
        httpReq.setHeader("Content-Type", "application/json");
        httpReq.setUri("/");
        byte[] asyncBytes = httpClient.getAsyncBytes(httpReq);
        System.out.println(new String(asyncBytes));

    }

}
