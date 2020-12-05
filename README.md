

<p align="center">
<a href="https://doc.hserver.top">
<img src="https://gitee.com/HServer/HServer/raw/master/doc/hserver.png" width="500" />
</a>
</p>
<p align="center">
    <a >
        <img src="https://img.shields.io/badge/Build-Java8-red.svg?style=flat" />
    </a>
     <a >
          <img src="https://img.shields.io/badge/HServer-2.9.56-yeoll.svg?style=flat" />
      </a>
    <a >
        <img src="https://img.shields.io/badge/Netty-4.1.53.Final-blue.svg" alt="flat">
    </a>
    <a >
        <img src="https://img.shields.io/badge/Licence-Apache2.0-green.svg?style=flat" />
    </a>

<p align="center">    
    <b>如果对您有帮助，您可以点右上角 "Star" 支持一下 谢谢！</b>
</p>



### 介绍
QQ交流群：*1065301527*

HServer是一个基于Netty的一个高并发Webserver，8核Linux 虚拟机 23w+ qps，像springboot一样开发的的web框架，

它提供了相关的注解和一些方法，完全能够完成我们大大小小的项目。作为一名Java程序员写web程序spring是我们项目99%会用的。

spring的优点就不过多讲，但是存在臃肿，太大太复杂，那我们就用一个简单框架快捷的HServer框架吧，它的qps比Servlet的容器更快

如果你是一个phper或者喜欢php，可以关注下 

[![黑小马工作室/HServer](https://gitee.com/heixiaomas/HServer/widgets/widget_card.svg?colors=ffffff,ffffff,,e3e9ed,666666,9b9b9b)](https://gitee.com/heixiaomas/HServer)
    

### 最新中央仓库版本
```xml
<dependency>
    <groupId>top.hserver</groupId>
    <artifactId>HServer</artifactId>
    <version>2.9.56</version>
</dependency>
```

### 资源菜单

| 资源名 | 地址 |
| :----:| :----: |
| 文档 | [点我](doc/HServer文档.md)  |
| 压测文档 | [点我](doc/PM.md) |
| Redis操作源码 | [点我](https://gitee.com/HServer/hserver-for-java-redis) |
| MYSQL-BeetlSQL(推荐)源码 | [点我](https://gitee.com/HServer/hserver-for-java-beetlsql) |
| MYSQL-NEO 源码 |  [点我](https://gitee.com/HServer/hserver-for-java-mysql) |
| RPC操作源码 | [点我](https://gitee.com/HServer/hserver-for-java-rpc) |
| HServer-Maven-Plugin | [点我](https://gitee.com/HServer/hserver-maven-plugin) |
| HServer-BeetlSql-Plugin | [点我](https://gitee.com/HServer/hserver-plugs-beetlsql) |
| HServer版本查询 | [点我](https://repo1.maven.org/maven2/top/hserver/HServer/) |



### 特点

* 简便易用5分钟即可掌握使用
* 快速构建高效API
* TCP层上直接构建
* Restful风格路由设计
* Cron定时器
* Filter拦截器
* DisruptorQueue队列
* HOOK组件
* Track组件
* Web Socket功能
* Proxy 自由处理
* RPC组件
* Nacos组件
* 热更新组件
* ApiDoc文档组件
* 权限组件
* Plugin组件自由扩展
* 高性能 100并发下8核qps 23w/s
* 高度自由度控制



### 原理与流程

![原理](https://gitee.com/HServer/HServer/raw/master/doc/%E6%9E%B6%E6%9E%84%E8%AF%B4%E6%98%8E1.jpg)

### 感受一个HelloWorld

**1.建立一个maven项目，导入依赖**

```xml
<dependency>
    <groupId>top.hserver</groupId>
    <artifactId>HServer</artifactId>
    <version>最新版</version>
</dependency>
```



**2.建立一个java包，如 com.test**

**3.建立一个主函数**

```java
@HServerBoot
public class WebApp {
    public static void main(String[] args) {
        HServerApplication.run(WebApp.class,8888,args);
    }
}
```

**4.建立一个控制器**

```java
@Controller
public class HelloController {

    @GET("/test1")
    public JsonResult test() {
        return JsonResult.ok();
    }
    
    @POST("/test2")
    public JsonResult b(HttpRequest request) {
        return JsonResult.ok().put("data",request.getRequestParams());
    }
    
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public JsonResult get() {
        return JsonResult.ok();
    }

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public JsonResult post(HttpRequest httpRequest) {
        return JsonResult.ok().put("data",httpRequest.getRequestParams());
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
```

**5.运行主函数，访问8888端口即可**



### 许可证

根据Apache许可证2.0版本（"许可证"）授权，为正常使用该服务，请确保许可证与本文件兼容。用户可通过以下链接获得许可证副本：

http://www.apache.org/licenses/LICENSE-2.0