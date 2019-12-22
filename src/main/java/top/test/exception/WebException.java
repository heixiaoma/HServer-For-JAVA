package top.test.exception;

import top.hserver.core.interfaces.GlobalException;
import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.server.context.Webkit;

@Bean
public class WebException implements GlobalException {

    @Override
    public void handler(Exception exception, Webkit webkit) {
        exception.printStackTrace();
        System.out.println(webkit.httpRequest.getUri() + "--->" + exception.getMessage());
        webkit.httpResponse.sendHtml("全局异常处理");
    }
}
