## **鉴权认证相关操作**

@RequiresPermissions

@RequiresRoles

@Sign

请使用相关注解对控制器的方法做标记，这样在执行到被注解标记的方法就会执行下面的相关方法
List<RouterPermission> routerPermissions = PermissionAdapter.getRouterPermissions();
通过上面的代码可以获取到所有标记的注解，他可以干嘛？
同步后台数据库里面的权限，后台管理面里面可以动态给角色分配权限。
自己做一个下拉选择列表，创建角色分配权限时，多选即可。


```java
@Bean
public class TestPermission implements PermissionAdapter {

    @Override
    public void requiresPermissions(RequiresPermissions requiresPermissions, Webkit webkit) {
        //这里你可以实现一套自己的权限检查算法逻辑，判断，
        //如果满足权限，不用其他操作，如果不满足权限，那么你可以通过，Webkit里面的方法直接输出相关内容
        //或者自定义一个异常类，在全局异常类做相关操作
        System.out.println(requiresPermissions.value()[0]);
    }

    @Override
    public void requiresRoles(RequiresRoles requiresRoles, Webkit webkit) {
        //这里你可以实现一套自己的角色检查算法逻辑，判断，
        //其他逻辑同上
        System.out.println(requiresRoles.value()[0]);
    }

    @Override
    public void sign(Sign sign, Webkit webkit) {
       //这里你可以实现一套自己的接口签名算法检查算法逻辑，判断，
       //其他逻辑同上
       Map<String, String> requestParams = webkit.httpRequest.getRequestParams();
       String sign1 = webkit.httpRequest.getHeader("sign");
       System.out.println(sign.value());
    }
}
```