package test1.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import top.hserver.core.interfaces.GlobalException;
import top.hserver.core.interfaces.HttpRequest;
import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.server.context.Webkit;

//@Bean
public class WebException implements GlobalException {

    @Override
    public void handler(Throwable throwable, int httpStatusCode,String errorDescription, Webkit webkit) {
        HttpRequest httpRequest = webkit.httpRequest;
        StringBuilder error=new StringBuilder();
        error.append("全局异常处理")
                .append("url")
                .append(httpRequest.getUri())
                .append("错误信息：")
                .append(throwable.getMessage())
                .append("错误描述：")
                .append(errorDescription);
        webkit.httpResponse.sendStatusCode(HttpResponseStatus.BAD_GATEWAY);
        webkit.httpResponse.sendText(error.toString());

    }
}
