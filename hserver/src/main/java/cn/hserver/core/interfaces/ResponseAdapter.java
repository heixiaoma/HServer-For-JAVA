package cn.hserver.core.interfaces;

import io.netty.handler.codec.http.FullHttpResponse;

/**
 * 响应数据拦截适配器
 *
 * @author hxm
 */
public interface ResponseAdapter {

    String result(String response);

    FullHttpResponse response(FullHttpResponse response);

}
