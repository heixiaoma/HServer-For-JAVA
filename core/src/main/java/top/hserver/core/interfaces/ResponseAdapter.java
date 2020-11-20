package top.hserver.core.interfaces;

import io.netty.handler.codec.http.FullHttpResponse;

import java.util.Map;

/**
 * 响应数据拦截适配器
 *
 * @author hxm
 */
public interface ResponseAdapter {

    String result(String response);

    FullHttpResponse response(FullHttpResponse response);

}
