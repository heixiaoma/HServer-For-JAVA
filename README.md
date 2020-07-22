# 

<p align="center">
<a href="https://hserver.top">
<img src="https://gitee.com/HServer/HServer/raw/master/doc/hserver.png" width="500" />
</a>
</p>
<p align="center">
    <a >
        <img src="https://img.shields.io/badge/Build-Java8-red.svg?style=flat" />
    </a>
     <a >
          <img src="https://img.shields.io/badge/HServer-2.9.35-yeoll.svg?style=flat" />
      </a>
    <a >
        <img src="https://img.shields.io/badge/Netty-4.1.50.Final-blue.svg" alt="flat">
    </a>
    <a >
        <img src="https://img.shields.io/badge/Licence-Apache2.0-green.svg?style=flat" />
    </a>

</p>
<p align="center">    
    <b>如果对您有帮助，您可以点右上角 "Star" 支持一下 谢谢！</b>
</p>



#### 介绍
    QQ交流群：1065301527
    HServer是一个基于Netty的一个高并发Webserver,它不仅仅是一个webserver，我们可以直接在这个基础上进行开发
    它提供了相关的注解和一些方法，完全能够完成我们大大小小的项目。作为一名Java程序员写web程序spring是我们项目99%会用的。
    spring的优点就不过多讲。Hserver是一个“tomcat”+“spring”的这样的小玩意。
    它的qps比tomcat更快，这个是因为Hserver使用的Netty,注解比spring更简，这是因为，它还不够强大（滑稽，我会努力让它更智能点）
    如果你是一个phper或者喜欢php，可以关注下 
[![黑小马工作室/HServer](https://gitee.com/heixiaomas/HServer/widgets/widget_card.svg?colors=ffffff,ffffff,,e3e9ed,666666,9b9b9b)](https://gitee.com/heixiaomas/HServer)
    
### 最新中央仓库版本
    <dependency>
        <groupId>top.hserver</groupId>
        <artifactId>HServer</artifactId>
        <version>2.9.35</version>
    </dependency>
    
    全部版本查询 https://repo1.maven.org/maven2/top/hserver/HServer/
### 压测结果，8核Linux 虚拟机 23w qps 恐怖至极（做api最适合了）
![AB测试](https://gitee.com/HServer/HServer/raw/master/doc/23.png)

详情请查看 [PM.md](doc/PM.md) 查看说明


### 更新历史
请查看 [CHANGELOG.md](doc/CHANGELOG.md) 了解近期更新情况。


### 文档地址
文档说明请查看 [点我](https://gitee.com/HServer/HServer/wikis/pages) 

Redis操作源码案例地址(Jedis) [点我](https://gitee.com/HServer/hserver-for-java-redis)

MYSQL操作源码案例地址(Neo) [点我](https://gitee.com/HServer/hserver-for-java-mysql)

MYSQL操作源码案例地址(BeetlSQL) [点我](https://gitee.com/HServer/hserver-for-java-beetlsql)

RPC操作源码案例地址(RPC) [点我](https://gitee.com/HServer/hserver-for-java-rpc)




## 快速开始
#### 1.文件结构框架架构说明
![AB测试](https://gitee.com/HServer/HServer/raw/master/doc/架构说明.png)
#### 2.注解认识
    以下注解基本模拟Spring的功能
    
    @Bean
    将Bean对象加入IOC容器中比如
        //默认按类型加入IOC容器
        @Bean
        class TestService{}
        //指定名字加入容器，装配的时候就只能通过名字装配了
        @Bean("testService1")
        class Test{}
        
    @Autowired
    自动装配注解
        //按类型注入
        @Autowired
        private TestService testService;
        //按Bean名字注入
        @Autowired("testServer1")
        private TestService testService; 
        
    @Controller
    控制器注解，将控制器加入IOC容器中，类似Spring mvc
    注解在类上面直接加上即可比如
        //Index控制器
        @Controller
        class IndexController{}、
        
    @GET,@POST,@RequestMapping
    方法注解，在@Controller注解类类中使用，标注一个方法为GET或者POST方法，例如
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
         
        //2.9.4+以上支持
        @RequestMapping(value = "/PUT", method = RequestMethod.PUT)
        public JsonResult PUT() {
          return JsonResult.ok();
        }
        //2.9.4+以上支持
        @RequestMapping(value = "/get_post", method = {RequestMethod.POST,RequestMethod.GET})
        public JsonResult get_post() {
          return JsonResult.ok();
        }
        
        //2.9.4+以上支持
        //全类型
        @RequestMapping(value = "/all")
        public JsonResult all() {
          return JsonResult.ok();
        }
        
        
      @Filter
      拦截器注解，标注一个类为拦截器，和JavaEE的Filter类似
          @Filter(1)//1表示拦截优先级，越小越优先
          public class MyFilter1 implements FilterAdapter {}
          //需要实现FilterAdapter接口
          
      @Hook
      hook注解就是Aop
          @Hook(value = Test.class, method = "show")
          public class HookTest implements HookAdapter {}
          //value表示aop的类,method要hook的方法，必须实现HookAdapter
  
  
    @Task
     定时任务
        @Task(name = "测试定时任务Cron", time ="*/5 * * * * ?")
        //标记在方法上，同时该类需要被@Bean 标记
        @Task(name = "测试定时任务1", time ="2000")
        public void timerTask() {}


    @WebSocket
    实现websocket通信
        @WebSocket("/ws")
        public class WebSocketTest implements WebSocketHandler {}
        //这样就可以完成基本的通信了


    @Configuration
    自定配置注解，需要配合@Bean注解一起使用，最后会把方法里面的返回的对象
    存储到IOC容器中，同时可以通过Autowired注解注入
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


    @RpcService
    标注一个Bean对象是一个rpc服务,也可以分配一个名字
        @Bean
        @RpcService
        public class RpcServiceTest {
            public String test(String name){
                return name+"我是RPC";
            }
        }  


    @Resource
    注入一个Rpc服务，也可以通过名字注入。详情，请看文档介绍   
        @Resource
        private RpcServiceTest rpcServiceTest;



    @Sign("MD5")
    @RequiresRoles("角色")
    @RequiresPermissions(value = {"/权限1","/权限2"}, logical=Logical.OR)
    该注解用于标注控制器里面的方法，方便自己实现sign签名算法，
    角色检查，权限检查，实现token等，详情下面的对应接口。


​                
#### 3.完成Hello World项目
     #第一步pom依赖引入
    
    <dependency>
        <groupId>top.hserver</groupId>
        <artifactId>HServer</artifactId>
        <version>最新版</version>
    </dependency>
    

    #第一步搞一个主函数
    public class WebApp {
        public static void main(String[] args) {
            HServerApplication.run(8888,args);
        }
    }
    
    #第二步同主函数建立一个包文件夹比如controller
    
    @Controller
    public class Hello {
    
        @GET("/hello")
        public Map index(HttpRequest request, String name) {
            Map<String, Object> res = new HashMap<>();
            res.put("code", 200);
            res.put("res", request.getRequestParams());
            res.put("msg", "Hello");
            return res;
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
    #就这样你就完成了一个简单得get请求定义，更多例子，可以参考包原理里测试包下面的例子

#### 4.文件上传下载操作

          #File类型得
          @GET("/downFile")
          public void downFile(HttpRequest request, HttpResponse response) {
              response.setDownloadFile(new File("D:\\Java\\HServer\\README.md"));
          }
          #InputStream 类型得
          @GET("/downInputStream")
          public void downInputStream(HttpRequest request, HttpResponse response) throws Exception {
              File file = new File("D:\\Java\\HServer\\README.md");
              InputStream fileInputStream = new FileInputStream(file);
              response.setDownloadFile(fileInputStream,"README.md");
          }
          
        @POST("/file")
        public Map file(HttpRequest request) {
            //单个文件
          FileItem file = request.queryFile("file");
          //多个文件上传
          Map<String, FileItem> fileItems = request.getFileItems();
          //然后对FileItem 进行保存，就可以了
          }

#### 5.Aop操作

        #必须实现HookAdapter的接口
        #同时被@Hook注解标注
        @Slf4j
        @Hook(value = Test.class, method = "show")
        public class HookTest implements HookAdapter {
        
            @Override
            public void before(Object[] objects) {
                log.info("aop.-前置拦截：" + objects[0]);
                objects[0]="666";
            }
        
            @Override
            public Object after(Object object) {
                return object + "aop-后置拦截";
            }
        }
#### 6.Filter操作

        #必须实现FilterAdapter接口，同时被@Filter标注，数字越小，优先级越高，切不要重复
        @Slf4j
        @Filter(1)
        public class MyFilter2 implements FilterAdapter {
            @Override
            public void doFilter(FilterChain chain, Webkit webkit) {
                log.info("MyFilter->1");
                chain.doFilter(webkit);
            }
        }
#### 7.定时任务操作

    #需要被@Bean注解标注,可以通过TaskManager类进行定时任务的控制，动态添加和删除
    @Bean
    public class TaskTest {
        
        @Autowired
        private TestService testService;
    
        private boolean flag = true;
    
        public void dynamicAddTimer() {
            System.out.println("动态添加定时任务");
            TaskManager.addTask("测试任务2", "2000", TestTask.class,"666");
        }

    ​        
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

#### 8.websocket操作

    #需要被@WebSocket标注同时给一个连接地址，最后实现WebSocketHandler接口，
    #Ws类定义了简单的发送方法，如果有其他的业务操作，可以获取ChannelHandlerContext，进行操作
    
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
#### 9.自带监控操作(2.9.25开始废除)
    application.properties文件配置
        #开启访问统计
        statistics=true
        #统计规则:以逗号分割的正则表达式
        statisticalRules=/hel.*,/admin/.*
    #StatisticsHandler操作
        #获取所有的IP地址
        StatisticsHandler.getIpMap()
        #请求队列（调用的URI，(发送大小，接收大小)宽带监视，耗时时间）
        StatisticsHandler.getLogRequestQue()
        #唯一IP请求的数量 uv
        StatisticsHandler.getUniqueIpCount()
        #请求总数   pv
        StatisticsHandler.getCount()
        #uri记录 被访问的记录（）
        StatisticsHandler.getUriData()
    #提示：
    1，如果自己要做统计，完全可以自定义一个定时器，动态保存数据哦
    2，StatisticsHandler,提供了一个remove方法,remove，用来清除，或者保存数据用，它会返回一个最新的数据同时清除自己
    3，如果开启统计，请务必，执行Remove方法，不然，内存可能就会蹦

#### 10.全局异常处理

    类必须要被@Bean注解，同时实现GlobalException接口，
    一个项目中最多只有一个GlobalException实现哦，可以没有.没有异常处理，同时又报错了，那么直接显示错误
    @Bean
    public class WebException implements GlobalException {
    
        @Override
        public void handler(Exception exception, Webkit webkit) {
            exception.printStackTrace();
            System.out.println(webkit.httpRequest.getUri() + "--->" + exception.getMessage());
            webkit.httpResponse.sendHtml("全局异常处理");
        }
    }

#### 11.服务器启动完成是执行的方法
     类必须要被@Bean注解，同时实现InitRunner接口，
     @Bean
     public class RunInit implements InitRunner {
     
         @Autowired
         private User user;
     
         @Override
         public void init(String[] args) {
             System.out.println("初始化方法：注入的User对象的名字是-->"+user.getName());
         }
     }

#### 12.鉴权认证相关操作

    //请使用相关注解对控制器的方法做标记，这样在执行到被注解标记的方法就会执行下面的相关方法
    //  List<RouterPermission> routerPermissions = PermissionAdapter.getRouterPermissions();
    // 通过上面的代码可以获取到所有标记的注解，他可以干嘛？
    // 同步后台数据库里面的权限，后台管理面里面可以动态给角色分配权限。
    // 自己做一个下拉选择列表，创建角色分配权限时，多选即可。
    
    /**
     * 验证逻辑请自己实现哦
     */
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

#### 13.RPC调用请看gitee的WIKi

#### 14.HServer2.9.4+后添加APIDOC生成功能
    详情请看WIKi
![AB测试](https://gitee.com/HServer/HServer/raw/master/doc/apidoc.png)


### 15.HServer2.9.9版本后添EventBus(订阅与发布，mq那种感觉)
    //有时候需要这样的一个简单功能，又不想导包，那就用这个盘他吧！
    //定义消费者
    @EventHandler("/aa/aa")
    public class EventTest{
    
        @Event("aa")
        public void aa(Map params) {
            try {
                System.out.println(Thread.currentThread().getName());
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    
        @Event("bb")
        public void bb(Map params) {
            try {
                System.out.println(Thread.currentThread().getName());
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
       //定义生产者
        @GET("/event")
        public JsonResult event(){
            Map params = new HashMap();
            params.put("a", "aaaaaaaaaa");
            params.put("b", 1234);
            params.put("c", 0);
            params.put("d", true);
            HServerEvent.sendEvent("/aa/aa/aa", params);
            return JsonResult.ok();
        }
        
        @GET("/queueSize")
        public JsonResult getQueueSize(){
            int size = HServerEvent.queueSize();
            return JsonResult.ok().put("size",size);
        }
    
    注意：队列已经进行持久，如果程序非正常退出，内存中的队列就会被丢弃，已经持久化的没有问题，下次启动会自动读取队列进行相关处理    
#### 16.Track跟踪
    使用动态字节码技术，在初始化对需要跟踪的方法进行，字节码处理，
    使用方法：
            1.在任意方法上添加，@Track 注解:例如
              @Track
              @GET("/track")
              public JsonResult track() {
                return JsonResult.ok();
              }


        ​            
    2.实现TrackAdapter接口，并在类上用 @Bean标识
        @Bean
        @Slf4j
        public class TrackImpl implements TrackAdapter {
            @Override
            public void track(Class clazz, Method method,StackTraceElement[] stackTraceElements, long start, long end) throws Exception {
                log.info("当前类：{},当前方法：{},耗时：{}", clazz.getName(), stackTraceElements[1].getMethodName(), (end - start) + "ms");
            }
        }
#### 17.单元测试技巧

        @RunWith(HServerTest.class)
        //指定一个外部的类，他会更具这个类递归扫描子类，并放入容器
        //@HServerBootTest(TestWebApp.class)
        public class test {
        
            @Autowired
            private User user;
        
            @Test
            public void main(){
                System.out.println(user.getName());;
            }
        }

#### 18.SSL配置
      
      在application.properties配置文件添加
      
      #举栗子：nginx版本的证书下载可能会得到 (xxx.pem或者xxx.cert) xxx.key
      #注意下载的证书中 key文件需要转换成 pk8 文件
      #因为netty4不支持pkcs12格式的私钥, 所以需要将私钥转换成pkcs8格式.
      #openssl pkcs8 -in my.key -topk8 -out my.pk8
      #转换过程需要你设置一个密码.
      
      方案一：
      #jar路径，证书文件应该放在\src\main\resources\ssl\ 目录里，打包会一起打包
      certPath=hserver.pem
      privateKeyPath=hserver.pk8
      privateKeyPwd=123
      
      方案二：
      #外置路径，指定一个绝对路径即可
      certPath=/home/ssl/hserver.pem
      privateKeyPath=/home/ssl/hserver.pk8
      privateKeyPwd=123

      然后监听443端口，你就能https 访问了。

#### 19.自定义方法级别注解

      //创建一个@Log 注解，该注解必须继承@Auto注解。例如下面.
      
      @Target(ElementType.METHOD)
      @Retention(RetentionPolicy.RUNTIME)
      @Documented
      @Auto
      public @interface Log {
      }
  
      
      //自己实现AnnotationAdapter接口同时使用@Bean标注 .例如
      @Bean
      public class MyAutoAnnotationAdapter implements AnnotationAdapter {
      
        @Override
        public void before(Annotation annotation, Object[] args, Class clazz, Method method) {
      
          System.out.println(annotation);
          System.out.println(args.length);
          if (args.length > 0) {
            System.out.println(args[0]);
          }
          System.out.println(clazz);
        }
        
        @Override
        public void after(Annotation annotation, Object object, Class clazz, Method method) {
          System.out.println(object);
        }
      }

      
      //然后你就能这个自己实现的接口中获取到.进行前置后置的处理.
      //通过参数Annotation 来判断是哪一个注解然后进行相关逻辑处理.

#### 20.自定义Banner
    resources文件夹里存放一个banner.txt 里面放入你图标就可以了.
    

#### 21技巧篇
    1. Linux 内核版本大于 2.5.44，(目前云服务器都有了，没有的话自己升级内核)的Linux默认使用epoll
    2.待更新 