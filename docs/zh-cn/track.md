## **Track跟踪**

使用动态字节码技术，字节码处理，可以跟踪任意方法，不跟踪HServer框架本身，不跟踪已经被jvm加载的，不跟踪已经被跟踪的类，不跟踪接口和抽象方法

1.在配置文件添加
```properties
track=true


#跟踪路径配置（可选）默认也有跟踪

#添加其他的包跟踪，用引英文逗号隔开默认不用在操作了，
#它是向下找，包名越短，扫码到的文件更多
trackExtPackages=com.mysql,org.freemarker

#排除这些包不跟踪
trackNoPackages=com.mysql,org.freemarker


```
2.实现TrackAdapter接口，并在类上用 @Bean标识

```java
/**
 * @author hxm
 */
@Bean
@Slf4j
public class TrackImpl implements TrackAdapter {
    @Override
    public void track(Class clazz, CtMethod method, StackTraceElement[] stackTraceElements, long start, long end,long pSpanId,long spanId) throws Exception {
        log.info("当前类：{},当前方法：{},耗时：{}", clazz.getName(), stackTraceElements[1].getMethodName(), (end - start) + "ms");
        JvmStack.printMemoryInfo();
        JvmStack.printGCInfo();
    }
}

```
