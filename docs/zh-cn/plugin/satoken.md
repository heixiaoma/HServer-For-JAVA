# SaToken

# 使用

```xml
<dependency>
    <groupId>cn.hserver</groupId>
    <artifactId>hserver-plugin-satoken</artifactId>
</dependency>
```

# 配置文件
- sa-token.properties
``` properties
# token 名称 (同时也是 cookie 名称)
tokenName=satoken
# token 有效期（单位：秒） 默认30天，-1 代表永久有效
timeout=2592000
# token 最低活跃频率（单位：秒），如果 token 超过此时间没有访问系统就会被冻结，默认-1 代表不限制，永不冻结
activeTimeout=-1
# 是否允许同一账号多地同时登录 （为 true 时允许一起登录, 为 false 时新登录挤掉旧登录）
isConcurrent=true
# 在多人登录同一账号时，是否共用一个 token （为 true 时所有登录共用一个 token, 为 false 时每次登录新建一个 token）
isShare=true
# token 风格（默认可取值：uuid、simple-uuid、random-32、random-64、random-128、tik）
tokenStyle=uuid
# 是否输出操作日志 
isLog=false

```
# 可注入类，如下

- SaSameTemplate
- SaJsonTemplate
- SaSignTemplate
- SaTempInterface
- SaTokenDao
- StpInterface

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