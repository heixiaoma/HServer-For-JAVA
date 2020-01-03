
## 压测

    通常不可能跑一个hello程序，都会伴随着业务，压测截图只是参考，框架qps本身很高,就看你怎么玩了

### 环境Win（Nio模型）
    win10,i7_9700k(8核16G)（每秒10W次左右）
##### 1.压测json
![AB测试](https://gitee.com/heixiaomas_admin/HServer/raw/master/doc/json.png)
##### 2.压测静态文件
![AB测试](https://gitee.com/heixiaomas_admin/HServer/raw/master/doc/file.png)
##### 3.模板引擎html
![AB测试](https://gitee.com/heixiaomas_admin/HServer/raw/master/doc/template.png)


### 环境Linux（Epoll模型）
    阿里云学生机 Centos7.3（1核2G内存）（每秒6W次左右）
##### 1.压测json
![AB测试](https://gitee.com/heixiaomas_admin/HServer/raw/master/doc/LinuxJson.png)
##### 2.压测静态文件
![AB测试](https://gitee.com/heixiaomas_admin/HServer/raw/master/doc/LinuxFile.png)
##### 3.模板引擎html
![AB测试](https://gitee.com/heixiaomas_admin/HServer/raw/master/doc/LinuxTemp.png)


### HServer2.2版本，环境Linux（Epoll模型）
    配置阿里云学生机 Centos7.3（1核2G内存）
    hello--> Requests per second:    73156.87 [#/sec] (mean)
    reids读--> Requests per second:    44103.39 [#/sec] (mean)
    redis写--> Requests per second:    38631.04 [#/sec] (mean) 
文档源码地址 [点我](https://gitee.com/heixiaomas_admin/hserver-for-java-redis)

### Hserver2.4版本，环境Linux（1核2G）
    RPC远程调用测试：Requests per second:    15709.09 [#/sec] (mean)
 