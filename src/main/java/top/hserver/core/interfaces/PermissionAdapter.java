package top.hserver.core.interfaces;

import top.hserver.core.ioc.annotation.RequiresPermissions;
import top.hserver.core.ioc.annotation.RequiresRoles;
import top.hserver.core.ioc.annotation.Sign;
import top.hserver.core.server.context.Webkit;

/**
 * 权限验证接口
 */
public interface PermissionAdapter {

    /**
     * 自定义实现权限检查
     *
     * @param requiresPermissions
     * @param webkit
     */
    void requiresPermissions(RequiresPermissions requiresPermissions, Webkit webkit);

    /**
     * 自定义实现角色检查
     *
     * @param requiresRoles
     * @param webkit
     */
    void requiresRoles(RequiresRoles requiresRoles, Webkit webkit);

    /**
     * 自定义实现sign检查
     *
     * @param sign
     * @param webkit
     */
    void sign(Sign sign, Webkit webkit);
}
