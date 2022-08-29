## 插件开发

1. 添加POM依赖 scope 设置为 provided

```xml

<dependency>
    <groupId>cn.hserver</groupId>
    <artifactId>hserver</artifactId>
    <version>${hserver.version}</version>
    <scope>provided</scope>
</dependency>
```

2. 实现接口PluginAdapter

```java
/**
 * @author hxm
 */

/**
 * 插件适配器
 *
 * @author hxm
 */
public interface PluginAdapter {

    /**
     * 启动APP时执行，此时日志类还没有被初始化，不要进行日志输出
     */
    void startApp();

    /**
     * 开始初始化
     */
    void startIocInit();

    /**
     * ioc初始化bean对象
     * @param classz
     * @return
     */
    boolean iocInitBean(Class classz);


    /**
     * 开始初始化获取初始化的被扫描的的对象
     * @param packageScanner
     */
    void iocInit(PackageScanner packageScanner);

    /**
     * 初始化完成
     */
    void iocInitEnd();

    /**
     * 开始注入
     */
    void startInjection();

    /**
     * 注入完成
     */
    void injectionEnd();

}

```

3. spi处理

```
建立一个文件  resources/META-INF/services/cn.hserver.core.interfaces.PluginAdapter
文件内容     cn.hserver.plugins.beetlsql.BeetLSqlPlugin
这个文件内容是你实现接口的包名+类名
```

4. 参考插件 https://gitee.com/HServer/hserver-plugs-beetlsql，或者在厂库里去找关于Plugin的代码。后期的插件会越来越多

