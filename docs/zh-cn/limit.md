## **并发限制**

HServer提供了GlobalLimit(全局QPS限制)和UrlLimit(更具URL来限制QPS)使用案例如下

- 继承方式

```java

import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.server.context.Webkit;
import top.hserver.core.server.util.JsonResult;

@Bean
public class TestLimit extends GlobalLimit{

    public TestLimit() {
        //限制 qps = 100/s 
        super(100);
    }

    @Override
    protected void result(Webkit webkit, boolean status) {
        if (status){
            webkit.httpResponse.sendJson(JsonResult.error("并发数限制"));
        }
    }
}

```

- 注解方式 可以使用在所有存放在容器的对象，比如service controller 等等
- @QpsLimit(qps = 1000)

```java

    @QpsLimit(qps = 1000)
    @GET("/b")
    public JsonResult b(){
        return JsonResult.ok();
    }

```
