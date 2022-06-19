
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
    class IndexController{}
    
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
    

    @Sign("MD5")
    @RequiresRoles("角色")
    @RequiresPermissions(value = {"/权限1","/权限2"}, logical=Logical.OR)
    //该注解用于标注控制器里面的方法，方便自己实现sign签名算法，
    //角色检查，权限检查，实现token等，详情下面的对应接口。
```
