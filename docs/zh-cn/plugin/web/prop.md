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
web.certPath=hserver.pem
web.privateKeyPath=hserver.pk8
web.privateKeyPwd=123
```



方案二：

```properties
  #外置路径，指定一个绝对路径即可
web.certPath=/home/ssl/hserver.pem
web.privateKeyPath=/home/ssl/hserver.pk8
web.privateKeyPwd=123
```




### 消息体大小，可以用于文件上传限制大小
```properties
#消息体最大值 默认int.maxValue
web.httpContentSize=999999
```

```properties
#业务线程池
### 业务线程数
### 业务线程数提示：使用了业务线程，整体QPS会有降低、
### 业务线程数优点：可以处理更多的并发耗时任务
### 业务线程数缺点：增加线程切换
web.businessPool=50
#读取限制 byte 单位
web.readLimit=100

#写出限制 byte 单位
web.writeLimit=100

#消息体大小 默认int.maxValue
web.httpContentSize=999999


#URL 全局更目录定义
web.rootPath=/app1

#-----------------ssl-------------
#注意下载的证书中 key文件需要转换成 pk8 文件
#因为netty4不支持pkcs12格式的私钥, 所以需要将私钥转换成pkcs8格式.
#openssl pkcs8 -in my.key -topk8 -out my.pk8
web.certPath=hserver.pem
web.privateKeyPath=hserver.pk8
web.privateKeyPwd=123

```





### 静态资源路径

```
resources/static
```

### 模板路径

```
resources/template
```
