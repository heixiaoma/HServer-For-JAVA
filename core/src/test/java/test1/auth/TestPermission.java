package test1.auth;

import top.hserver.core.interfaces.PermissionAdapter;
import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.ioc.annotation.RequiresPermissions;
import top.hserver.core.ioc.annotation.RequiresRoles;
import top.hserver.core.ioc.annotation.Sign;
import top.hserver.core.server.context.Webkit;
import top.hserver.core.server.util.JsonResult;

/**
 * 验证逻辑请自己实现哦
 */
@Bean
public class TestPermission implements PermissionAdapter {

    @Override
    public void requiresPermissions(RequiresPermissions requiresPermissions, Webkit webkit) throws Exception {
            System.out.println(1/0);
            webkit.httpResponse.sendJson(JsonResult.ok());
            System.out.println(requiresPermissions.value()[0]);
    }

    @Override
    public void requiresRoles(RequiresRoles requiresRoles, Webkit webkit) throws Exception {
        System.out.println(requiresRoles.value()[0]);
    }

    @Override
    public void sign(Sign sign, Webkit webkit) throws Exception {
        System.out.println(sign.value());
    }
}
