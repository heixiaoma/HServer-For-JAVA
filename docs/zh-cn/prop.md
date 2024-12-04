## **自定义Banner**
resources文件夹里存放一个banner.txt 里面放入你图标就可以了.


```properties
#外置配置大于jar中包配置文件，配置文件大于代码默认设置
#动态配置文件
#环境切换
#在app.properties文件中添加,env=dev
#配置文件app-dev.properties也会加载在里面
#或者java -jar -Denv=dev xxx.jar 启动参数指定env
env=dev

#应用名字，默认HServer
appName=HServer

#hum消息端口，默认9527
humPort=9527

#端口动态指定，多个用英文逗号隔开 配置文件大于代码写死，
ports=9090

#链路跟踪 默认不跟踪
track=true

#添加其他的包跟踪，用引英文逗号隔开默认不用在操作了，前缀匹配模式

#它是向下找，包名越短，扫码到的文件更多
trackExtPackages=com.mysql,org.freemarker

#排除这些包不跟踪
trackNoPackages=com.mysql,org.freemarker

#taskPool定时任务线程池子配置，默认大小是cpu核心数+1
taskPool=5

#bossPool Netty boss线程组大小 默认cpu 核心数两倍
bossPool=2

#workerPool Netty worker线程组大小 默认核心数两倍
workerPool=2


#backlog 指定了内核为此套接口排队的最大连接个数；
#对于给定的监听套接口，内核要维护两个队列: 未连接队列和已连接队列
#backlog 的值即为未连接队列和已连接队列的和。
backLog=8192

#io模型默认 IO_URING >EPOLL>KQUEUE>JDK
ioMode=DEFAULT

#前置协议的最大大小，用于自定义拦截协议时的分析，如果1024字节都不能判断你的大小，可以将其调整大点
preProtocolMaxSize=1024

#日志级别 debug info error ...
level=debug
#自定义日志名字
logbackName=logback-dev.xml

#队列数据缓存位置 默认当前项目下
persistPath=/user/mcl/data

```
