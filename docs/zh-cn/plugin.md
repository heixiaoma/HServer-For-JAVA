
##  插件开发

1. 添加POM依赖 scope 设置为 provided

```xml
        <dependency>
            <groupId>top.hserver</groupId>
            <artifactId>HServer</artifactId>
            <version>${HServer.version}</version>
            <scope>provided</scope>
        </dependency>
```

2. 实现接口PluginAdapter

```java
/**
 * @author hxm
 */
public class BeetLSqlPlugin implements PluginAdapter {

    private static final Logger log = LoggerFactory.getLogger(BeetLSqlPlugin.class);


    @Override
    public void startIocInit() {

    }

    @Override
    public void iocInitEnd() {

    }

    @Override
    public void startInjection() {

    }

    @Override
    public void injectionEnd() {
      
    }

}
```

3. spi处理

```
建立一个文件  resources/META-INF/services/top.hserver.core.interfaces.PluginAdapter
文件内容     cn.hserver.plugins.beetlsql.BeetLSqlPlugin
这个文件内容是你实现接口的包名+类名
```

4. 参考插件 https://gitee.com/HServer/hserver-plugs-beetlsql，或者在厂库里去找关于Plugin的代码。后期的插件会越来越多

