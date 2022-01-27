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

### 日志设置
```properties
level=error
logbackName=logback-dev.xml
```

### 全局流量整形
```properties
#读取限制 byte 单位
readLimit=100

#写出限制 byte 单位
writeLimit=100
```

### 业务线程数
提示：使用了业务线程，整体QPS会有降低、
优点：可以处理更多的并发耗时任务
缺点：增加线程切换

```
#businessPool 业务线程大小，默是50，当添加这个配置，就视为生效,小于0 使用woker线程池（性能最高，一旦阻塞就完蛋，这将对编程有一定的要求，如果在控制器层全部设计成异步操作，使用这个配置是最香的）
businessPool=50
```

### 消息体大小，可以用于文件上传限制大小
```properties
#消息体最大值 默认int.maxValue
httpContentSize=999999
```


### EPOLL模式
```
#可以开启Epoll时是否开启epoll 默认true
epoll=true
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




### 全部配置
```properties
#外置配置大于jar中包配置文件，配置文件大于代码默认设置

#应用名字，默认HServer
appName=HServer

#hum消息端口，默认9527
humPort=9527

#端口动态指定，多个用英文逗号隔开 配置文件大于代码写死，
ports=9090

#动态配置文件
env=dev

#openHttp2 true  当https模式下开启http2功能
openHttp2=true

#链路跟踪 默认不跟踪
track=true

#添加其他的包跟踪，用引英文逗号隔开默认不用在操作了，
#它是向下找，包名越短，扫码到的文件更多
trackExtPackages=com.mysql,org.freemarker

#taskPool定时任务线程池子配置，默认大小是cpu核心数+1
taskPool=5

#bossPool Netty boss线程组大小 默认2
bossPool=2

#workerPool Netty worker线程组大小 默认2
workerPool=2

#businessPool 业务线程大小，默是用的workerPool -1标识使用workerPool 性能更快
businessPool=50

#可以开启Epoll时是否开启epoll 默认true
epoll=true

#读取限制 byte 单位
readLimit=100

#写出限制 byte 单位
writeLimit=100

#日志级别 debug info error ...
level=debug

#消息体大小 默认int.maxValue
#httpContentSize=999999

#队列数据缓存位置 默认当前项目下
persistPath=/user/mcl/data


#-----------------ssl-------------
#注意下载的证书中 key文件需要转换成 pk8 文件
#因为netty4不支持pkcs12格式的私钥, 所以需要将私钥转换成pkcs8格式.
#openssl pkcs8 -in my.key -topk8 -out my.pk8
certPath=hserver.pem
privateKeyPath=hserver.pk8
privateKeyPwd=123
```

