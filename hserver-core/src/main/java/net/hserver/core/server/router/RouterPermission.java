package net.hserver.core.server.router;

import io.netty.handler.codec.http.HttpMethod;
import net.hserver.core.ioc.annotation.RequiresPermissions;
import net.hserver.core.ioc.annotation.RequiresRoles;
import net.hserver.core.ioc.annotation.Sign;

/**
 * @author hxm
 */
public class RouterPermission {
    private String url;
    private RequiresPermissions requiresPermissions;
    private RequiresRoles requiresRoles;
    private Sign sign;
    private HttpMethod reqMethodName;
    private String controllerPackageName;
    private String controllerName;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RequiresPermissions getRequiresPermissions() {
        return requiresPermissions;
    }

    public void setRequiresPermissions(RequiresPermissions requiresPermissions) {
        this.requiresPermissions = requiresPermissions;
    }

    public RequiresRoles getRequiresRoles() {
        return requiresRoles;
    }

    public void setRequiresRoles(RequiresRoles requiresRoles) {
        this.requiresRoles = requiresRoles;
    }

    public Sign getSign() {
        return sign;
    }

    public void setSign(Sign sign) {
        this.sign = sign;
    }

    public HttpMethod getReqMethodName() {
        return reqMethodName;
    }

    public void setReqMethodName(HttpMethod reqMethodName) {
        this.reqMethodName = reqMethodName;
    }

    public String getControllerPackageName() {
        return controllerPackageName;
    }

    public void setControllerPackageName(String controllerPackageName) {
        this.controllerPackageName = controllerPackageName;
    }

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }
}
