## **Track跟踪**

使用动态字节码技术，在初始化对需要跟踪的方法进行，字节码处理，可以跟踪任意方法
1.在任意方法上添加，@Track 注解:例如

```java
@Track
@GET("/track")
public JsonResult track() {
    return JsonResult.ok();
}
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
    public void track(Class clazz, CtMethod method, StackTraceElement[] stackTraceElements, long start, long end) throws Exception {
        log.info("当前类：{},当前方法：{},耗时：{}", clazz.getName(), stackTraceElements[1].getMethodName(), (end - start) + "ms");
        JvmStack.printMemoryInfo();
        JvmStack.printGCInfo();
    }
}

```
