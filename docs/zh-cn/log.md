
## **日志拦截**

类必须要被@Bean注解，同时实现LogAdapter接口，操作吃接口不要阻塞，
可以用HServerQueue 转发出来处理，切记不能让这里卡住了

- logback-hserver.xml中默认已经加入这个配置，用于扩展日志，此处了解即可
```xml
<appender name="HSERVER_EXT" class="top.hserver.core.log.HServerLogAsyncAppender">
    </appender>
```

- 你需要完成下面的实现就可以进行拦截了
```java
@Bean
public class Log implements LogAdapter {
    @Override
    public void log(LoggingEvent loggingEvent) {
        System.out.println(loggingEvent.getMessage());
    }
}
```

通过配置文件也可以来控制日志级别
```properties
#日志级别 debug info error ...
log=debug
```