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
    待更新
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
    待更新
#### 4.文件上传下载操作
    待更新    
#### 5.Aop操作
    待更新
#### 6.Filter操作
    待更新
#### 7.定时任务操作
    待更新
#### 8.websocket操作
    待更新
#### 9.自带监控操作
    待更新
#### 10.集群分布式监控操作
    待更新
#### 11.技巧篇
    1.Linux上使用Epoll提高性能
    2.待更新
