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

## 注解认识

根据上面的例子，大家应该理解是非常容易的，和springboot很相似。接下我们了解下注解这里，注解只是做简单描述，具体使用在后面的章节会演示出来

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
|      @Configuration      | 配置注解，这个和springboot有相识之处（这个类中可以注入 @NacosClass,@NacosValue,@Value,@ConfigurationProperties这些注解产生的对象） |
| @ConfigurationProperties |     配置类，和springboot相似 将Properties转为对象放入IOC     |
|       @Controller        |  标记类为控制器 @Controller 参数可以指定一个URL 和 一个名字  |
|          @Hook           |                         AOP操作使用                          |
|   @RequiresPermissions   |                           权限注解                           |
|      @RequiresRoles      |                           角色注解                           |
|        @Resource         |               RPC对象的注入使用.可以按名字注入               |
|       @RpcService        |         标记一个Service是一个RPC服务，可以给一个名字         |
|          @Sign           | 作用在控制器方法上.可以根据他来实现sign检查当然你可以用拦截器自己处理 |
|          @Task           |                    定时器使用，具体看例子                    |
|          @Track          | 链路跟踪注解，如果你想检查某些方法的耗时或者其他监控，可以用这个注解，具体看下面的介绍 |
|          @Value          |             用来把Properties字段注入到类的字段里             |
|        @WebSocket        |               websocket注解，具体看下面的介绍                |
|            @QueueListener            |                            标记一个类为队列处理类                            |
|            @QueueHandler            |                           标记一个方法为队列处理方法                           |
| @AssertFalse | 字段为必须为false  |
| @AssertTrue |字段为必须为true|
| @Length |字段CharSequence 类型的长度必须是 length 长|
| @Max |字段值必须大于这个值，number|
| @Min |字段值必须小于这个值，number|
| @NotBlank |字段不能为null同时不是 ""|
| @NotEmpty |CharSequence 集合 map 数组 不是null 长度或者size 大于0|
| @NotNull |字段不能为Null|
| @Null |字段必须为Null|
| @Pattern |字段CharSequence 必须满足这个正则|
| @Size |字段 CharSequence 集合 map 数组必须在这范围内|
| @ApiImplicitParams |API生成标记|
| @ApiImplicitParam |API生成标记|



## 常见用例

```java
//以下注解基本模拟Spring的功能

	//@Bean
	//将Bean对象加入IOC容器中比如
    //默认按类型加入IOC容器
    @Bean
    class TestService{}
    //指定名字加入容器，装配的时候就只能通过名字装配了
    @Bean("testService1")
    class Test{}
    
	//@Autowired
	//自动装配注解
    //按类型注入
    @Autowired
    private TestService testService;
    //按Bean名字注入
    @Autowired("testServer1")
    private TestService testService; 
    
	//@Controller
	//控制器注解，将控制器加入IOC容器中，类似Spring mvc
	//注解在类上面直接加上即可比如
    //Index控制器
    @Controller
    class IndexController{}、
    
	//@GET,@POST,@RequestMapping
	//方法注解，在@Controller注解类类中使用，标注一个方法为GET或者POST方法，例如
    @GET("/index")
    public void index(){}  
    @POST("/index")
    public void index(){}
    
    //url规则匹配
    @GET("/url1/{url}")
    public String url(HttpRequest httpRequest){
        String url = httpRequest.query("url");
        System.out.println(url);
        return url;
    }

    @GET("/url/{url}")
    public String url(String url){
        return "匹配到的URL:"+url;
    }

    @POST("/a/{url}/bb")
    public String ab(String url){
        return "匹配到的URL:"+url;
    } 
     
    @RequestMapping(value = "/PUT", method = RequestMethod.PUT)
    public JsonResult PUT() {
      return JsonResult.ok();
    }
    @RequestMapping(value = "/get_post", method = {RequestMethod.POST,RequestMethod.GET})
    public JsonResult get_post() {
      return JsonResult.ok();
    }
    
    //全类型
    @RequestMapping(value = "/all")
    public JsonResult all() {
      return JsonResult.ok();
    }
    
    
  
  	//拦截器注解，标注一个类为拦截器，和JavaEE的Filter类似
      @Bean
      public class MyFilter1 implements FilterAdapter {}
      //需要实现FilterAdapter接口
      
  	//@Hook
 	// hook注解就是Aop
      @Hook(value = Test.class)
      public class HookTest implements HookAdapter {}
      //value表示aop的类,method要hook的方法，必须实现HookAdapter


	//@Task
 	//定时任务
    @Task(name = "测试定时任务Cron", time ="*/5 * * * * ?")
    //标记在方法上，同时该类需要被@Bean 标记
    @Task(name = "测试定时任务1", time ="2000")
    public void timerTask() {}


	//@WebSocket
	//实现websocket通信
    @WebSocket("/ws")
    public class WebSocketTest implements WebSocketHandler {}
    //这样就可以完成基本的通信了


	//@Configuration
	//自定配置注解，需要配合@Bean注解一起使用，最后会把方法里面的返回的对象
	//存储到IOC容器中，同时可以通过Autowired注解注入
    @Configuration
    public class DataConfig {
    
        //自定义名字（用例：比如多数据源注入）
        @Bean("createUser")
        public User createUser(){
            User user = new User();
            user.setAge(999);
            user.setName("我是配置类自定义名字的数据");
            user.setSex("未知");
            return user;
        }
    
       //按类型存储 
        @Bean
        public User createUser1(){
            User user = new User();
            user.setAge(999);
            user.setName("我是配置类的默认数据");
            user.setSex("未知");
            return user;
        }
    
    }


	//@RpcService
	//标注一个Bean对象是一个rpc服务,也可以分配一个名字
    @Bean
    @RpcService
    public class RpcServiceTest {
        public String test(String name){
            return name+"我是RPC";
        }
    }  


	//@Resource
	//注入一个Rpc服务，也可以通过名字注入。详情，请看文档介绍   
    @Resource
    private RpcServiceTest rpcServiceTest;



    @Sign("MD5")
    @RequiresRoles("角色")
    @RequiresPermissions(value = {"/权限1","/权限2"}, logical=Logical.OR)
    //该注解用于标注控制器里面的方法，方便自己实现sign签名算法，
    //角色检查，权限检查，实现token等，详情下面的对应接口。
```





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

**5.运行主函数，访问8888端口即可**

## 文件上传下载

```java
    /**
     * 文件下载
     *
     * @param response
     * @return
     */
    @GET("/downFile")
    public void downFile(HttpRequest request, HttpResponse response) {
        response.setDownloadFile(new File("D:\\Java\\HServer\\README.md"));
    }

    @GET("/downInputStream")
    public void downInputStream(HttpRequest request, HttpResponse response) throws Exception {
        File file = new File("D:\\Java\\HServer\\README.md");
        InputStream fileInputStream = new FileInputStream(file);
        response.setDownloadFile(fileInputStream, "README.md");
    }

    /**
     * 上传文件测试
     *
     * @param request
     * @return
     */
    @POST("/file")
    public Map file(HttpRequest request) {

        Map<String, PartFile> fileItems = request.getMultipartFile();
        fileItems.forEach((k, v) -> {
            System.out.println(k);
            System.out.println(v);
            byte[] data = v.getData();
            System.out.println(data);
        });
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("res", request.getRequestParams());
        res.put("msg", test1q.show("xx"));
        return res;
    }
```

## Hook操作

提供hook注解，它只能Hook在ioc中存在的bean对象. hook功能除了hook指定的类所有方法，还能hook注解，只要包含这个注解的类都会被hook.

```java
import top.hserver.core.interfaces.HookAdapter;
import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.Hook;
import test1.service.HelloService;
import test1.service.Test;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * hook 指定的Test类，在调用Test的所有方法 都会进入before 和 after，可以通过Method 选择处理或者不处理.
 */
@Slf4j
@Hook(Test.class)
public class HookTest implements HookAdapter {

    @Autowired
    private HelloService helloService;

    @Override
    public void before(Class clazz, Method method, Object[] objects) {
        log.debug("aop.-前置拦截111111111111111111111");
    }

    @Override
    public Object after(Class clazz, Method method,Object object) {
        return object + "aop-后置拦截1111111111111111"+helloService.sayHello();
    }

    @Override
    public void throwable(Class clazz, Method method, Throwable throwable) {
        System.out.println(throwable);
    }
}

```



```java
import lombok.extern.slf4j.Slf4j;
import test1.log.Log;
import test1.service.HelloService;
import test1.service.Test;
import top.hserver.core.interfaces.HookAdapter;
import top.hserver.core.ioc.annotation.*;
import java.lang.reflect.Method;
/**
 * hook 指定用了@Log的类，只要用的@Log的类都会被Hook住。这个功能主要用途在做一些 自定义注解时比较常用.比如做一个@log 日志打印注解 或者 耗时统计注解.
 */
@Slf4j
@Hook(value = Log.class)
public class HookTest2 implements HookAdapter {

    @Autowired
    private HelloService helloService;

    @Override
    public void before(Class clazz, Method method, Object[] objects) {
        log.debug("aop.-前置拦截 {}",method.getName());
    }

    @Override
    public Object after(Class clazz, Method method,Object object) {
        log.debug("aop.-后置拦截 {}",object);
        return object;
    }

    @Override
    public void throwable(Class clazz, Method method, Throwable throwable) {
        System.out.println(throwable);

    }
}

```

上面测试例子都是HServer Test包里的Test1文件中，有兴趣的可以去运行体验哈

## Filter拦截器

拦截器的使用主要是用在跨域等操作，或者其他拦截，

接口可以实现多个 ，但是得只要有输出将会中断链式调用.

```java
/**
 * @author hxm
 */
@Bean
public class GlobalPermissionFilter implements FilterAdapter {

    @Autowired
    private TokenService tokenService;

    @Override
    public void doFilter(Webkit webkit) throws Exception {
        webkit.httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        webkit.httpResponse.setHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS,DELETE");
        webkit.httpResponse.setHeader("Access-Control-Allow-Credentials", " true");
        webkit.httpResponse.setHeader("Access-Control-Allow-Headers", " Content-Type,Content-Length,Accept-Encoding,Accept,X-Requested-with, Origin,Access-Token,X-Access-Token,x-access-token,miniType,mini-type");
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



## 定时器

```java
@Bean
public class TaskTest {
    
    @Autowired
    private TestService testService;

    private boolean flag = true;

    public void dynamicAddTimer() {
        System.out.println("动态添加定时任务");
        TaskManager.addTask("测试任务2", "2000", TestTask.class,"666");
    }
    
    
    @Task(name = "测试定时任务1", time ="*/5 * * * * ?")
    public void timerTask() {
        System.out.println("测试定时任务，注入的对象调用结果:" + testService.testa());
        if (flag) {
            dynamicAddTimer();
            flag = false;
        }
    }

    @Task(name = "测试定时任务2", time = "2000")
    public void removeTask() {
        //干掉方法注解版本
        boolean task1 = TaskManager.removeTask("测试定时任务1");
        //干掉动态添加的
        boolean task2 = TaskManager.removeTask("测试任务2");
        //干掉自己
        boolean task3 = TaskManager.removeTask("测试定时任务2");
        //结果
        System.out.println("任务已经被干掉了 tash1=" + task1 + ",task2=" + task2 + ",task3=" + task3);
    }

}

//动态添加定时任务的实现类必须要实现一个TaskJob,样才能被TaskManager管理
//添加任务 TaskManager.addTask("测试任务2", "2000", TestTask.class,"666");
//删除任务  boolean is_success = TaskManager.removeTask("测试任务2");
public class TestTask implements TaskJob {

    @Override
    public void exec(Object... args) {
        String args_ = "";
        for (Object arg : args) {
            args_ += arg.toString();
        }
        System.out.println("测试定时器动态添加任务，参数是：" + args_);
    }
}
```



##	WebSocket

需要被@WebSocket标注同时给一个连接地址，最后实现WebSocketHandler接口，
Ws类定义了简单的发送方法，如果有其他的业务操作，可以获取ChannelHandlerContext，进行操作

```java


@WebSocket("/ws")
public class WebSocketTest implements WebSocketHandler {

    @Autowired
    private TestService testService;

    @Override
    public void onConnect(Ws ws) {
        System.out.println("连接成功,分配的UID：" + ws.getUid());
    }

    @Override
    public void onMessage(Ws ws) {
        ws.send("666" + testService.testa() + ws.getUid());
        System.out.println("收到的消息,"+ws.getMessage()+",UID：" + ws.getUid());
    }

    @Override
    public void disConnect(Ws ws) {
        System.out.println("断开连接,UID:" + ws.getUid());
    }
}
```



## **全局异常处理**

类必须要被@Bean注解，同时实现GlobalException接口.

异常接口可以实现多个 ，但是得只要有输出将会中断链式调用.

```java

@Bean
public class WebException implements GlobalException {
    @Override
    public void handler(Throwable throwable, int httpStatusCode, String errorDescription, Webkit webkit) {
        HttpRequest httpRequest = webkit.httpRequest;
        StringBuilder error = new StringBuilder();
        error.append("全局异常处理")
                .append("url")
                .append(httpRequest.getUri())
                .append("错误信息：")
                .append(throwable.getMessage())
                .append("错误描述：")
                .append(errorDescription);
        webkit.httpResponse.sendStatusCode(HttpResponseStatus.BAD_GATEWAY);
        webkit.httpResponse.sendText(error.toString());
    }
}

```

## **服务器启动完成是执行的方法**

 类必须要被@Bean注解，同时实现InitRunner接口，

```java
 @Bean
 public class RunInit implements InitRunner {
 
     @Autowired
     private User user;
 
     @Override
     public void init(String[] args) {
         System.out.println("初始化方法：注入的User对象的名字是-->"+user.getName());
     }
 }
```

## **鉴权认证相关操作**

@RequiresPermissions

@RequiresRoles

@Sign

请使用相关注解对控制器的方法做标记，这样在执行到被注解标记的方法就会执行下面的相关方法
  List<RouterPermission> routerPermissions = PermissionAdapter.getRouterPermissions();
 通过上面的代码可以获取到所有标记的注解，他可以干嘛？
同步后台数据库里面的权限，后台管理面里面可以动态给角色分配权限。
 自己做一个下拉选择列表，创建角色分配权限时，多选即可。


```java
@Bean
public class TestPermission implements PermissionAdapter {

    @Override
    public void requiresPermissions(RequiresPermissions requiresPermissions, Webkit webkit) {
        //这里你可以实现一套自己的权限检查算法逻辑，判断，
        //如果满足权限，不用其他操作，如果不满足权限，那么你可以通过，Webkit里面的方法直接输出相关内容
        //或者自定义一个异常类，在全局异常类做相关操作
        System.out.println(requiresPermissions.value()[0]);
    }

    @Override
    public void requiresRoles(RequiresRoles requiresRoles, Webkit webkit) {
        //这里你可以实现一套自己的角色检查算法逻辑，判断，
        //其他逻辑同上
        System.out.println(requiresRoles.value()[0]);
    }

    @Override
    public void sign(Sign sign, Webkit webkit) {
       //这里你可以实现一套自己的接口签名算法检查算法逻辑，判断，
       //其他逻辑同上
       Map<String, String> requestParams = webkit.httpRequest.getRequestParams();
       String sign1 = webkit.httpRequest.getHeader("sign");
       System.out.println(sign.value());
    }
}
```

## **RPC调用**

hserver 提供了两种模式 第一种默认模式不需要注册中心 第二种模式是需要Nacos注册中心. 编码过程中没有什么差异

主要差异是在配置上. 文档我怕讲不清楚，这里给出了一个项目 master分支是使用的默认模式 nacos 分支是用的是nacos。

详细请下载源码学习使用 地址:https://gitee.com/HServer/hserver-for-java-rpc



## **ApiDoc生成功能**

  _关于这个api文档生成目前只是一个简洁版，在未来日子里相信会变得更好_ 

第一步

```java
@Controller(value = "/v1/Api2", name = "Api接口2")
class ApiController{}
//value值会自动补全类中方法的URL，name值，在文档中有名字定义的作用，如果这个名字不定义，那么会采用控制器的全路径。
```

第二步

```java
在需要生成注解的方法上，添加这个注解，这个注解类似swagger的注解。

  @GET("/get")
  @ApiImplicitParams(
    value = {
      @ApiImplicitParam(name = "name", value = "名字", required = true, dataType = DataType.String),
      @ApiImplicitParam(name = "sex", value = "性别", required = true, dataType = DataType.Integer),
      @ApiImplicitParam(name = "age", value = "年龄", required = true, dataType = DataType.Integer),
    },
    note = "这是一个Api的Get方法",
    name = "api获取GET"
  )
  public JsonResult get(User user) {
    return JsonResult.ok().put("data", user);
  }
```

第三步

HServer提供了一个叫ApiDoc的类，对他进行实例化，就可以获取到生成文档的对象，你可以进行自己的文档生成定制，

或者使用HServer提供 的简洁版本的文档模板 hserver_doc.ftl 需要将依赖里的这个文件copy到你的模板里面.

下面就是例子，ApiDoc的构造器可以传入class类型，或者传入String类型，主要目的是进行扫包，可以直接传入包名，或者传入class，然后获取包名


```java
  //官方模板输出
  @GET("/api")
  public void getApiData(HttpResponse httpResponse) {
    //ApiDoc apiDoc = new ApiDoc("top.test");
    ApiDoc apiDoc = new ApiDoc(TestWebApp.class);
    try {
      List<ApiResult> apiData = apiDoc.getApiData();
      HashMap<String,Object> stringObjectHashMap=new HashMap<>();
      stringObjectHashMap.put("data",apiData);
      httpResponse.sendTemplate("hserver_doc.ftl",stringObjectHashMap);
    }catch (Exception e){
      httpResponse.sendJson(JsonResult.error());
    }
  }
  
  //输出json,或者自己自定名字  
  @GET("/apiJson")
  public JsonResult getApiDataa() {
    ApiDoc apiDoc = new ApiDoc("top.test");
    try {
      List<ApiResult> apiData = apiDoc.getApiData();
      return JsonResult.ok().put("data",apiData);
    }catch (Exception e){
      return JsonResult.error();
    }
  }
```
![AB测试](https://gitee.com/HServer/HServer/raw/master/doc/apidoc.png)



## 消息队列的使用

队列核心技术使用的是disruptor

生产者

```java
  @GET("/event")
    public JsonResult event() {
    	//队列名字，方法参数
        HServerQueue.sendQueue("Queue", "666");
        return JsonResult.ok();
    }


    @GET("/eventInfo")
    public JsonResult eventInfo() {
        QueueInfo queueInfo = HServerQueue.queueInfo("Queue");
        return JsonResult.ok().put("data", queueInfo);
    }

```

消费者定义

```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface QueueListener {
    /**
     * 队列名
     *
     * @return
     */
    String queueName();

    /**
     * 消费者类型
     *
     * @return
     */
    QueueHandlerType type() default QueueHandlerType.NO_REPEAT;

    /**
     * 队列默认长度
     * @return
     */
    int bufferSize() default 1024;
}
```

我们在使用该注解时，会考虑到队列的数据 重复消费还是不重复消费。我们可以指定 type类型就可以了，默认是不重复消费的

```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface QueueHandler {
    //消费者优先级 级别重小到大排序，小的有限，同一样的就并行操作
    int level() default 1;

    //消费者数量
    int size() default 1;

}
```

消费方法定义，消费者数量默认是1，我们可以定义多个，通过size参数处理

level级别,当里面出现两个 我们指定级别实现 顺序消费.

我们可以通过消费类型和消费级别可以自由组合，多种方案



```java
import lombok.extern.slf4j.Slf4j;
import test1.service.HelloService;
import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.queue.QueueHandler;
import top.hserver.core.ioc.annotation.queue.QueueListener;

import java.util.concurrent.atomic.LongAdder;

@Slf4j
@QueueListener(queueName = "Queue")
public class EventTest {

    @Autowired
    private HelloService helloService;

    LongAdder atomicLong = new LongAdder();

    @QueueHandler(level = 1, size = 2)
    public void aa(String name) {
        atomicLong.increment();
        System.out.println(atomicLong + "---------" + Thread.currentThread().getName());
    }


    @QueueHandler(level = 2, size = 2)
    public void cc(String name) {
        atomicLong.increment();
        System.out.println(atomicLong + "---------" + Thread.currentThread().getName());
    }
}

```



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



## **单元测试**

1. web 服务测试

```java
/**
 * @author hxm
 */
@RunWith(HServerTestServer.class)
public class TestWebApp {

  @Test
  public void start(){
  }

}
```

2. 非web服务测试

```java

@RunWith(HServerTest.class)
public class test2 {

  @Autowired
  private TestBean testBean;

  @Autowired
  private Tom tom;

  @Test
  public void test(){
    System.out.println(testBean.hello());
  }

  @Test
  public void test2(){
    System.out.println(tom.toString());
  }

}
```



## **自定义Banner**

resources文件夹里存放一个banner.txt 里面放入你图标就可以了.

## **app.properties配置文件说明**

### 环境切换

在app.properties文件中添加,env=dev

配置文件app-dev.properties也会加载在里面

或者java -jar -Denv=dev xxx.jar 启动参数指定env

### SSL支持

  在app.properties配置文件添加

  #举例：nginx版本的证书下载可能会得到 (xxx.pem或者xxx.cert) xxx.key
  #注意下载的证书中 key文件需要转换成 pk8 文件
  #因为netty4不支持pkcs12格式的私钥, 所以需要将私钥转换成pkcs8格式.
  #openssl pkcs8 -in my.key -topk8 -out my.pk8
  #转换过程需要你设置一个密码.

  方案一：

```properties
  #jar路径，证书文件应该放在\src\main\resources\ssl\ 目录里，打包会一起打包
  certPath=hserver.pem
  privateKeyPath=hserver.pk8
  privateKeyPwd=123
```



  方案二：

```properties
  #外置路径，指定一个绝对路径即可
  certPath=/home/ssl/hserver.pem
  privateKeyPath=/home/ssl/hserver.pk8
  privateKeyPwd=123
```



  然后监听443端口，你就能https 访问了。

### 定时器线程数

```properties
#taskPool定时任务线程池子配置，默认大小是cpu核心数+1
taskPool=5
```



### BOSS线程组大小

```properties
#bossPool Netty boss线程组大小 默认2，可以按cpu 核心数来
bossPool=2
```



### worker线程组大小

```properties
#workerPool Netty worker线程组大小 默认4
workerPool=4
```

### 业务线程数

提示：使用了业务线程，整体QPS会有降低、
        优点：可以处理更多的并发耗时任务
        缺点：增加线程切换
        建议:在非耗时任务情况下不建议配置此选项，当然根据业务而定

```
#businessPool 业务线程大小，默是用的workerPool，当添加这个配置，就视为生效
businessPool=50
```



### EPOLL模式

```
#可以开启Epoll时是否开启epoll 默认true
epoll=true
```

### **配置中心**

```properties
在app.properties文件中添加
#配置中心地址
app.nacos.config.address=127.0.0.1:8848
就可以使用动态配置注解，配置中心更新，服务自动刷新.
标记一个类
@NacosClass
标记一个字段
@NacosValue

目前Nacos中Text类型是@NacosValue使用，Json和properties 被@NacosClass使用
```





## 配置类注解

1. app.properties文件内容

```properties
app.name=张三

mysql.url=jdbc.....
mysql.userName=root
mysql.password=root
```

2. 配置

```java

@ConfigurationProperties( prefix = "mysql")
class MysqlConfig{
    private String name;
    private String userName;
    private String password;
    
    get...
    set...
}


```

3. 使用

```java
@Value("app.name")
private String name;

@Autowired
private MysqlConfig mysqlConfig;


```

## 



## **参数校验器**

```
控制器参数是一个Bean时，字段可以使用校验器注解
```

| 注解         | 描述                                                   |
| ------------ | ------------------------------------------------------ |
| @AssertFalse | 字段为必须为false                                      |
| @AssertTrue  | 字段为必须为true                                       |
| @Length      | 字段CharSequence 类型的长度必须是 length 长            |
| @Max         | 字段值必须大于这个值，number                           |
| @Min         | 字段值必须小于这个值，number                           |
| @NotBlank    | 字段不能为null同时不是 ""                              |
| @NotEmpty    | CharSequence 集合 map 数组 不是null 长度或者size 大于0 |
| @NotNull     | 字段不能为Null                                         |
| @Null        | 字段必须为Null                                         |
| @Pattern     | 字段CharSequence 必须满足这个正则                        |
| @Size        | 字段 CharSequence 集合 map 数组必须在这范围内          |



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
文件内容     net.hserver.plugins.beetlsql.BeetLSqlPlugin
这个文件内容是你实现接口的包名+类名
```

4. 参考插件 https://gitee.com/HServer/hserver-plugs-beetlsql，或者在厂库里去找关于Plugin的代码。后期的插件会越来越多

## **细节和注意事项**

### 在Controller层中定义的方法

参数可以是基础数据类型或者bean对象，或者HttpRequest，HttpResponse，（需要是这个包下面的top.hserver.core.interfaces.的对象）当不是表单提交时，可以通过httpRequest.getRawData()，获取到请求的数据。默认也会尝试将内容转成对象

### 打包jar

只需要在pom.xml 添加打包命令即可，打包之前记得 *clean*

```xml
    <build>
        <plugins>
            <plugin>
                <groupId>net.hserver.plugins.maven</groupId>
                <artifactId>hserver-maven-plugin</artifactId>
                <version>1.0</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```

### 静态资源路径

```
resources/static
```

### 模板路径

```
resources/template
```

### 查看相关实例代码

```
找到Maven的依赖包，在top.hserver.test.目录下是大量的测试案例和代码可以查询学习和使用。
```



