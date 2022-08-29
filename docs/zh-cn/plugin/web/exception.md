
## **全局异常处理**

类必须要被@Bean注解，同时实现GlobalException接口.

异常接口可以实现多个 ，但是得只要有输出将会中断链式调用.

```java

@Bean
public class WebException implements GlobalException {
    @Override
    public void handler(Throwable throwable, int httpStatusCode, String errorDescription, Webkit webkit) {
        HttpRequest httpRequest = webkit.httpRequest;
        StringBuilder error = new StringBuilder();
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

```