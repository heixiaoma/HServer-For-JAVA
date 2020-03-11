package top.hserver.core.server.router;

import io.netty.handler.codec.http.HttpMethod;
import lombok.Data;
import top.hserver.core.ioc.annotation.RequiresPermissions;
import top.hserver.core.ioc.annotation.RequiresRoles;
import top.hserver.core.ioc.annotation.Sign;

@Data
public class RouterPermission {
    private String url;
    private RequiresPermissions requiresPermissions;
    private RequiresRoles requiresRoles;
    private Sign sign;
    private HttpMethod reqMethodName;
    private String controllerPackageName;
    private String controllerName;

}
