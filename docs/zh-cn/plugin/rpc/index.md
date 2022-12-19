# hserver-rpc-plugin

## 不错得，纯异步RPC组件，性能管够

```xml
<!--远程调用框架-->
<dependency>
  <groupId>cn.hserver</groupId>
  <artifactId>hserver-plugin-rpc</artifactId>
</dependency>
<!--服务发现注册-->
<dependency>
    <groupId>cn.hserver</groupId>
    <artifactId>hserver-plugin-cloud</artifactId>
</dependency>
<!--服务发现注册nacos-->
<dependency>
    <groupId>cn.hserver</groupId>
    <artifactId>hserver-plugin-nacos</artifactId>
</dependency>

```

##### 服务都注册到注册中心上去 消费和提供者都注册进去
```properties
app.cloud.reg.registerAddress=http://127.0.0.1:8848
#注册名字
app.cloud.reg.registerName=dsds
#注册我的Ip
app.cloud.reg.registerMyIp=127.0.0.1
#注册我的端口
app.cloud.reg.registerMyPort=8080
#注册分组
app.cloud.reg.groupName=DEFAULT_GROUP
```

```java
//调用服务
public @interface Resource {
    String value() default "";

    //服务名
    String serverName();

    //组名
    String groupName() default "";
}

@Resource(serverName="user")
private UserService userService;



//发布服务
public @interface RpcService {
    String value() default "";
}

@Bean
@RpcService
public class UserServiceImpl implements UserService {
}
//服务调用需要定义common接口UserService
```