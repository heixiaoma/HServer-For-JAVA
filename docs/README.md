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

**已经有parent时可以用dependencyManagement进行处理**

```xml
<parent>
    <groupId>com.example.demo</groupId>
    <artifactId>demo-parent</artifactId>
    <version>demo</version>
</parent>

<dependencyManagement>
    <dependencies>
        <dependency>
            <artifactId>hserver-parent</artifactId>
            <groupId>cn.hserver</groupId>
            <version>最新版本</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>


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
