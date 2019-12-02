# HServer

#### 介绍
    基于Netty而做的一个高并发Webserver
    如果你是一个phper或者喜欢php，可以关注下 
[![黑小马工作室/HServer](https://gitee.com/heixiaomas/HServer/widgets/widget_card.svg?colors=ffffff,ffffff,,e3e9ed,666666,9b9b9b)](https://gitee.com/heixiaomas/HServer)
    
### 压测说明
请查看 [PM.md](doc/PM.md) 查看说明


### 更新历史
请查看 [CHANGELOG.md](doc/CHANGELOG.md) 了解近期更新情况。


## 快速开始
#### 1.文件结构框架架构说明
![AB测试](https://gitee.com/heixiaomas_admin/HServer/raw/master/doc/架构说明.png)
#### 2.注解认识
    以下注解基本模拟Spring的功能
    @Bean
    将Bean对象加入IOC容器中比如
        //按默名字加入IOC容器
        @Bean
        class TestService{}
        //指定名字加入容器，装配的时候就只能通过名字装配了
        @Bean("testService")
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
        class IndexController{}   
    @GET,@POST
    方法注解，在@Controller注解类类中使用，标注一个方法为GET或者POST方法，例如
        @GET("/index")
        public void index(){}  
        @POST("/index")
        public void index(){}  
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
        @Task(name = "测试定时任务", time = 1000 * 2)
        //标记在方法上，同时该类需要被@Bean 标记
        @Task(name = "测试定时任务1", time = 1000 * 2)
        public void timerTask() {}
    @WebSocket
    实现websocket通信
        @@WebSocket("/ws")
        public class WebSocketTest implements WebSocketHandler {}
        //这样就可以完成基本的通信了
#### 3.完成Hello World项目

    #第一步搞一个主函数
    public class WebApp {
        public static void main(String[] args) {
            HServerApplication.run(WebApp.class, 8888);
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
            res.put("msg", test1q.show("xx"));
            res.put("name", name);
            return res;
        }
    #就这样你就完成了一个简单得get请求定义
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
            TaskManager.addTask("测试任务2", 1000 * 2, TestTask.class,"666");
        }
        
        
        @Task(name = "测试定时任务1", time = 1000 * 2)
        public void timerTask() {
            System.out.println("测试定时任务，注入的对象调用结果:" + testService.testa());
            if (flag) {
                dynamicAddTimer();
                flag = false;
            }
        }
    
        @Task(name = "测试定时任务2", time = 1000 * 10)
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
#### 9.自带监控操作
    待更新，和开发
#### 10.集群分布式监控操作
    待更新
#### 11.技巧篇
    1.Linux上使用Epoll提高性能
    2.待更新
