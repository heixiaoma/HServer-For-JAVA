<p align="center">
<a href="http://docs.hserver.cn">
<img src="https://gitee.com/HServer/HServer/raw/master/doc/icon.png" width="500" />
</a>
</p>
<p align="center">
    <a >
        <img src="https://img.shields.io/badge/Build-Java8-red.svg?style=flat" />
    </a>
    <a >
        <img src="https://img.shields.io/badge/Netty-4.1.112.Final-blue.svg" alt="flat">
    </a>
    <a >
        <img src="https://img.shields.io/badge/Licence-Apache2.0-green.svg?style=flat" />
    </a>

<p align="center">    
    <b>如果对您有帮助，您可以点右上角 "Star" 支持一下 谢谢！</b>
</p>

### 介绍

QQ交流群：*1065301527*

HServer是一个基于Netty开发的一个功能强大，资源丰富，开发灵活，轻量级，低入侵，高并发的新型Web开发框架.


### 最新央仓库统一版本

| 资源名 |  版本号   |
| :----:|:------:|
| HServer版本 | 3.6.M3 |

### 资源菜单

|             资源名              |                            地址                             |
|:----------------------------:|:---------------------------------------------------------:|
|             教程文档             |               [点我](http://docs.hserver.cn)                |
|             压测文档             |                      [点我](doc/PM.md)                      |
|          Redis使用案例           |  [点我](https://gitee.com/HServer/hserver-for-java-redis)   |
|     MYSQL-BeetlSQL 使用案例      | [点我](https://gitee.com/HServer/hserver-for-java-beetlsql) |
|    MYSQL-MybatisPlus 使用案例    |      [点我](https://gitee.com/HServer/hserver-system)       |
|        MYSQL-NEO 使用案例        |  [点我](https://gitee.com/HServer/hserver-for-java-mysql)   |
|   redis(redisson) 操作 使用案例    |  [点我](https://gitee.com/HServer/hserver-for-java-redis)   |
|        MongoDb操作 使用案例        | [点我](https://gitee.com/HServer/hserver-for-java-mongodb)  |
|         HServer版本查询          |     [点我](https://repo1.maven.org/maven2/cn/hserver/)      |

### 特点

* 简便易用5分钟即可掌握使用
* 快速构建高效API
* TCP层上直接构建
* Restful风格路由设计
* Cron定时器
* Filter拦截器
* 持久Queue队列
* HOOK/AOP组件
* Track链路跟踪组件
* Web Socket功能
* Mqtt WebSocketMqtt功能
* 自定义协议
* Proxy 自由处理
* ApiDoc文档组件
* 权限组件
* Plugin组件自由扩展
* HUM消息
* 高性能
* 高度自由度控制
* 流量整形
* Netty 原生响应支持自己扩展

### 概念图

![原理](https://gitee.com/HServer/HServer/raw/master/doc/planning_map.jpg)

### 压测数据 DeePin 8h 16g i7-9700k

worker线程池
![原理](https://gitee.com/HServer/HServer/raw/master/doc/w.png)

默认配置50个业务线程池
![原理](https://gitee.com/HServer/HServer/raw/master/doc/b.png)

### 感受一个HelloWorld

**1.建立一个maven项目，导入依赖**

```xml

<parent>
    <artifactId>hserver-parent</artifactId>
    <groupId>cn.hserver</groupId>
    <version>最新版本</version>
</parent>

<dependencies>
<!--    核心依赖-->
    <dependency>
        <artifactId>hserver</artifactId>
        <groupId>cn.hserver</groupId>
    </dependency>
<!--    web框架 -->
    <dependency>
        <artifactId>hserver-plugin-web</artifactId>
        <groupId>cn.hserver</groupId>
    </dependency>
</dependencies>
<!--    打包jar -->
<build>
    <plugins>
        <plugin>
            <artifactId>hserver-plugin-maven</artifactId>
            <groupId>cn.hserver</groupId>
        </plugin>
    </plugins>
</build>


```

**2.建立一个java包，如 com.test**

**3.建立一个主函数**

```java

@HServerBoot
public class WebApp {
    public static void main(String[] args) {
        HServerApplication.run(WebApp.class, 8888, args);
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
        return JsonResult.ok().put("data", request.getRequestParams());
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public JsonResult get() {
        return JsonResult.ok();
    }

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public JsonResult post(HttpRequest httpRequest) {
        return JsonResult.ok().put("data", httpRequest.getRequestParams());
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
        Map<String, Object> obj = new HashMap<>();
        obj.put("user", user);
//        httpResponse.sendTemplate("/admin/user/list.ftl", obj);
        httpResponse.sendTemplate("a.ftl", obj);
    }
}
```

**5.运行主函数，访问8888端口即可**

### 许可证

根据Apache许可证2.0版本（"许可证"）授权，为正常使用该服务，请确保许可证与本文件兼容。用户可通过以下链接获得许可证副本：

http://www.apache.org/licenses/LICENSE-2.0
