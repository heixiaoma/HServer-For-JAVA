## 远程调用
- 配置
```java

@Bean
public class ForestConfig extends ForestClientConfig {
  
    @Override
    public void config(ForestConfiguration forestConfiguration) {
        forestConfiguration.setVariableValue("apiBaseUrl","http://127.0.0.1:8080");
    }
}
```

- 接口定义
```java
@ForestClient
@BaseRequest(
        baseURL = "{apiBaseUrl}"
)
public interface TestRemote {
    /**
     * 查询签到
     * @param address
     * @return
     */
    @Get("/data/pub/blockSign")
    String blockSign(@Query("address")String address);

    /**
     * 购买金币
     * @param data
     * @return
     */
    @Post("/data/pub/buyCoin")
    String buyCoin(@Body Map<String, String> data);
}
```

- 使用

```java
@Bean
@Slf4j
public class GameService {
    @Autowired
    private TestRemote testRemote;

    //todo 其他业务调用
}
```
