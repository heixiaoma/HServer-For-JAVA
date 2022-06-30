## Redis操作

### 导入依赖
```xml
<!-- https://mvnrepository.com/artifact/org.redisson/redisson -->
    <dependency>
        <groupId>org.redisson</groupId>
        <artifactId>redisson</artifactId>
        <version>3.16.5</version>
    </dependency>
```

### 添加配置类

```java
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import cn.hserver.core.ioc.annotation.Bean;
import cn.hserver.core.ioc.annotation.Configuration;
import cn.hserver.core.ioc.annotation.Value;

/**
 * @author hxm
 */
@Configuration
public class RedissonConfig {
    /**
     * redis.address=127.0.0.1:6379
     * redis.password=Root123@.
     * redis.database=0
     */
    @Value("redis.address")
    private String address;

    @Value("redis.password")
    private String password;

    @Value("redis.database")
    private Integer database;

    @Bean
    public RedissonClient redissonClient() {
        try {
            Config config = new Config();
            config.setCodec(new JsonJacksonCodec());
            config.useSingleServer().setAddress("redis://" + address).setDatabase(database).setPassword(password);
            return Redisson.create(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
```


### 然后使用
```java

    @Autowired
    private RedissonClient redissonClient;
    
    //举栗子伪代码
    public QaQuestion getQuestion() throws Exception {
        RLock lock = redissonClient.getLock(ConstData.QUESTION_KEY + ":" + id);
        try {
            if (lock.tryLock(0, 3, TimeUnit.SECONDS)) {
                //todo
            }
        } catch (Throwable e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            if (lock.isLocked()) {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

```

### 官方API使用文档
```text
https://github.com/redisson/redisson/wiki/目录
```