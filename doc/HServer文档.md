# HServer文档

## 序言

### HServer是什么
HServer是一个简单高效的web库基于Netty开发.它不遵循servlet规范.可以理解为mini版本的springboot的版本.使用它开发可以在初期获得很大的QPS。会随着业务变化QPS慢慢降低.它给我们的起点很高，剩下就是我们自己高效运用即可.
如果你正在使用该框架建议加入QQ群，我们可以更好的交流
QQ交流群：1065301527
### Hserver的理念 

**极简、高性能、分布式**
**极简** 代码只有200多KB 更多的案例会在gitee.com/Hserver 的组下给出案例源码.
**高性能** 使用Netty网络库作为核心，比起传统的web容器性能高数十倍.
**分布式** 支持RPC模式，可以实现分布式调用.

### 使用该框架的公司
深圳市快读科技，深圳市聚美良品科技，广州家庭医生在线，上海互软集团，深圳市巨和网络

## 原理与流程
![原理](https://gitee.com/HServer/HServer/raw/master/doc/%E6%9E%B6%E6%9E%84%E8%AF%B4%E6%98%8E1.jpg)



## 快速开始

**1.建立一个maven项目，导入依赖**

```xml
<dependency>
    <groupId>top.hserver</groupId>
    <artifactId>HServer</artifactId>
    <version>最新版</version>
</dependency>
```



**2.建立一个java包，如 com.test**

**3.建立一个主函数**

```java
public class WebApp {
    public static void main(String[] args) {
        HServerApplication.run(WebApp.class,8888,args);
    }
}
```

**4.建立一个控制器**

```java
@Controller
public class HelloController {

    @GET("/test1")
    public JsonResult test() {
        return JsonResult.ok();
    }
    
    @POST("/test2")
    public JsonResult b(HttpRequest request) {
        return JsonResult.ok().put("data",request.getRequestParams());
    }
    
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public JsonResult get() {
        return JsonResult.ok();
    }

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public JsonResult post(HttpRequest httpRequest) {
        return JsonResult.ok().put("data",httpRequest.getRequestParams());
    }
    
    /**
     * 模板测试
     * @param httpResponse
     */
    @GET("/template")
    public void template(HttpResponse httpResponse) {
        User user = new User();
        user.setAge(20);
        user.setName("xx");
        user.setSex("男");
        Map<String,Object> obj=new HashMap<>();
        obj.put("user",user);
//        httpResponse.sendTemplate("/admin/user/list.ftl", obj);
        httpResponse.sendTemplate("a.ftl", obj);
    }
}
```



## 注解认识

更具上面的例子，大家应该理解是非常容易的，和springboot很相似.接下我们了解哈注解这里注解只是做简单描述，具体使用在后面的章节会演示出来

|           注解           |                           描述信息                           |
| :----------------------: | :----------------------------------------------------------: |
|          @Bean           | 将当前类加入IOC中，类似spring的@Component注解，可以名字放入ioc 如@Bean("Test") |
|        @Autowired        | 将ioc里的某个对象注入给某个字段，和spring用法类似,可以名字注入ioc 如@Autowired("Test") |
|     @RequestMapping      |                 这个和springmvc的注解很相识                  |
|           @GET           |                 请求类型注解类似@GetMapping                  |
|          @POST           |                         请求类型注解                         |
|           @PUT           |                         请求类型注解                         |
|          @HEAD           |                         请求类型注解                         |
|          @PATCH          |                         请求类型注解                         |
|         @DELETE          |                         请求类型注解                         |
|         @OPTIONS         |                         请求类型注解                         |
|         @CONNECT         |                         请求类型注解                         |
|          @TRACE          |                         请求类型注解                         |
|        @BeetlSQL         | 对Beetlsql支持，类似@Repository，Dao层，具体案例看 Beetlsql案例源码 |
|      @Configuration      | 配置注解，这个和springboot有相识之处（这个类中可以注入 @NacosClass,@NacosValue,@Value,@ConfigurationProperties这些注解产生的对象） |
| @ConfigurationProperties |     配置类，和springboot相似 将Properties转为对象放入IOC     |
|       @Controller        |  标记类为控制器 @Controller 参数可以指定一个URL 和 一个名字  |
|         @Filter          | 标记当前类是一个拦截器，同时可以指定一个参数设置优先级,越小越优先 |
|          @Hook           |                         AOP操作使用                          |
|   @RequiresPermissions   |                           权限注解                           |
|      @RequiresRoles      |                           角色注解                           |
|        @Resource         |               RPC对象的注入使用.可以按名字注入               |
|       @RpcService        |         标记一个Service是一个RPC服务，可以给一个名字         |
|          @Sign           | 作用在控制器方法上.可以更具他来实现sign检查当然你可以用拦截器自己处理 |
|          @Task           |                    定时器使用，具体看例子                    |
|          @Track          | 链路跟踪注解，如果你想检查某些方法的耗时或者其他监控，可以用这个注解，具体看下面的介绍 |
|          @Value          |             用来把Properties字段注入到类的字段里             |
|        @WebSocket        |               websocket注解，具体看下面的介绍                |
|            @EventHandler            |                            标记一个类为事件类                            |
|            @Event            |                           标记一个方法为事件处理方法                           |
| @AssertFalse | 字段为必须为false  |
| @AssertTrue |字段为必须为true|
| @Length |字段CharSequence 类型的长度必须是 length 长|
| @Max |字段值必须大于这个值，number|
| @Min |字段值必须小于这个值，number|
| @NotBlank |字段不能为null同时不是 ""|
| @NotEmpty |CharSequence 集合 map 数组 不是null 长度或者size 大于0|
| @NotNull |字段不能为Null|
| @Null |字段必须为Null|
| @Pattern |段CharSequence 必须满足这个正则|
| @Size |字段 CharSequence 集合 map 数组必须在这范围内|
| @ApiImplicitParams |API生成标记|
| @ApiImplicitParam |API生成标记|