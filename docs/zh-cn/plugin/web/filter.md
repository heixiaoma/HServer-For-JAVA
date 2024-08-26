
## Filter拦截器

拦截器的使用主要是用在跨域等操作，或者其他拦截，

接口可以实现多个 ，但是得只要有输出将会中断链式调用.

```java
/**
 * @author hxm
 */
@Bean
@Order(1)
public class CorsFilter implements FilterAdapter {
    @Override
    public void doFilter(Webkit webkit) throws Exception {
        webkit.httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        webkit.httpResponse.setHeader("Access-Control-Allow-Methods", "*");
        webkit.httpResponse.setHeader("Access-Control-Allow-Credentials", "*");
        webkit.httpResponse.setHeader("Access-Control-Allow-Headers", "*");
        if (webkit.httpRequest.getRequestType().equals(HttpMethod.OPTIONS)) {
            webkit.httpResponse.sendHtml("");
        }
    }
}
```



## 响应拦截器

```java
import io.netty.handler.codec.http.FullHttpResponse;
import top.hserver.core.interfaces.ResponseAdapter;
import top.hserver.core.ioc.annotation.Bean;

@Bean
public class MyResponse implements ResponseAdapter {
    @Override
    public String result(String response) {
        //可以拿到 String数据 ，可以做一些替换操作 ，比如 国际化之类的。
        // 文件操作不会进入这里
        System.out.println(response);
        return response;
    }

    @Override
    public FullHttpResponse response(FullHttpResponse response) {
        //Netty 的对象 最后一次经过这里 就会write出去
        System.out.println(response);
        return response;
    }
}

```

