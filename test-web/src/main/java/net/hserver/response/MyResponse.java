package net.hserver.response;

import io.netty.handler.codec.http.FullHttpResponse;
import top.hserver.core.interfaces.ResponseAdapter;
import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.ioc.annotation.Order;

@Bean
@Order(1)
public class MyResponse implements ResponseAdapter {
    @Override
    public String result(String response) {
        //可以拿到 String数据 ，可以做一些替换操作 ，比如 国际化之类的。
        // 文件操作不会进入这里
        System.out.println("1---"+response);
        return response;
    }

    @Override
    public FullHttpResponse response(FullHttpResponse response) {
        //Netty 的对象 最后一次经过这里 就会write出去
        System.out.println(response);
        return response;
    }
}
