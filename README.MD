# HServer

#### 介绍
    基于Netty而做的一个高并发Webserver
    如果你是一个phper或者喜欢php，可以关注下 https://gitee.com/heixiaomas/HServer
    
### 压测截图

##### 1.压测json
![AB测试](https://gitee.com/heixiaomas_admin/HServer/raw/master/doc/json.png)
##### 2.压测静态文件
![AB测试](https://gitee.com/heixiaomas_admin/HServer/raw/master/doc/file.png)
##### 3.模板引擎html
![AB测试](https://gitee.com/heixiaomas_admin/HServer/raw/master/doc/template.png)

## 快速开始
#### 1.文件结构框架架构说明
    待更新
#### 2.注解认识
    待更新
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

## 更新历史
### 2019-10-23
    完成系统的IOC容器和bean的装配,控制器方法的装配已经Json的处理

### 2019-11-8
    完成系统的异常处理，可以实现GET，POST提交了，暂时不支持文件的上传下载和定时器
    
### 2019-11-9
    添加HServer 首页模板，对文件下载处理进行优化
    
### 2019-11-10
    对POST的处理修复，可以上传文件和对参数优化，对控制的方法，参数的基础类型增加

### 2019-11-13        
    构建返回对象的异常捕捉，和添加Response的header头设置功能 ,添加文件下载功能
    
### 2019-11-14
    对控制器参数优化，加入javassist,获取参数类型名字和后期HOOK准备      

### 2019-11-15
    依赖注入调配控制，使用javassist实现Aop功能 
    
### 2019-11-16
    完成Filter功能，优化了下pom文件

### 2019-11-18
    调整文件接口，和进一步抽象,Filter的测试 ,完成free marker集成