# SaToken

# 使用

```xml
<dependency>
    <groupId>cn.hserver</groupId>
    <artifactId>hserver-plugin-satoken</artifactId>
</dependency>
```


# 可注入类，如下

- SaSameTemplate
- SaJsonTemplate
- SaSignTemplate
- SaTempInterface
- SaTokenDao
- StpInterface
- SaTokenSecondContextCreator
- SaTokenListener
- SaAnnotationHandlerInterface
- SaTempInterface
- SaJsonTemplate
- SaSameTemplate
- SaSameTemplate
- SaTokenConfig
- SaHttpBasicTemplate
- SaHttpBasicTemplate
- SaOAuth2Template
- SaOAuth2ServerConfig
- SaOAuth2DataLoader
- SaOAuth2ScopeHandlerInterface
- SaSsoServerTemplate
- SaSsoClientTemplate
- SaSsoServerConfig
- SaSsoClientConfig


```java

@Bean
public class StpInterfaceImpl  implements StpInterface {
    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        System.out.println(1222);
        // 本 list 仅做模拟，实际项目中要根据具体业务逻辑来查询权限
        List<String> list = new ArrayList<String>();
        list.add("101");
        list.add("user.add");
        list.add("user.update");
        list.add("user.get");
        // list.add("user.delete");
        list.add("art.*");
        return list;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        System.out.println(122224);
        // 本 list 仅做模拟，实际项目中要根据具体业务逻辑来查询角色
        List<String> list = new ArrayList<String>();
        list.add("admin");
        list.add("super-admin");
        return list;
    }

}


```

# 使用注解如下，兼容所有注解

```java
@Controller
public class TestController {

    @SaCheckPermission("user.add")
    @GET("/get")
    public JsonResult get() {
        return JsonResult.ok();
    }

    @GET("/login")
    public JsonResult login() {
        StpUtil.login(10001);
        return JsonResult.ok();
    }
}
```

# 关于拦截示例
```java

@Bean
public class SaFilter implements FilterAdapter {
    @Override
    public void doFilter(Webkit webkit) throws Exception {
        SaRouter.match("/get1", r -> {
            StpUtil.checkPermission("user");
        });
    }
}

```

# 实现了redisDao版本,其他版本请自行使用 SaTokenDao实现

- redis版本依赖Redisson只需要集成然后在项目里添加这段代码即可

```java
@Bean
public class RedisTokenDao extends SaTokenDaoRedis {

}
```

# 权限全局拦截

- 通过捕获异常实现拦截数据

```java

@Bean
public class Ex implements GlobalException {
    @Override
    public void handler(Throwable throwable, int i, String s, Webkit webkit) {
        if (throwable instanceof NotPermissionException) {
            NotPermissionException throwable1 = (NotPermissionException) throwable;
            throwable1.printStackTrace();
        }
        webkit.httpResponse.sendHtml("失败");
    }
}

```