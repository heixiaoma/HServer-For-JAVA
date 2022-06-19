package net.hserver.core.interfaces;

import net.hserver.core.ioc.annotation.RequiresPermissions;
import net.hserver.core.ioc.annotation.RequiresRoles;
import net.hserver.core.ioc.annotation.Sign;
import net.hserver.core.server.context.Webkit;
import net.hserver.core.server.router.RouterManager;
import net.hserver.core.server.router.RouterPermission;

import java.util.List;

/**
 * 权限验证接口
 * @author hxm
 */
public interface PermissionAdapter {

    /**
     * 自定义实现权限检查
     * @param requiresPermissions
     * @param webkit
     * @throws Exception
     */
    void requiresPermissions(RequiresPermissions requiresPermissions, Webkit webkit) throws Exception;

    /**
     * 自定义实现角色检查
     *
     * @param requiresRoles
     * @param webkit
     * @throws Exception
     */
    void requiresRoles(RequiresRoles requiresRoles, Webkit webkit) throws Exception;

    /**
     * 自定义实现sign检查
     *
     * @param sign
     * @param webkit
     * @throws Exception
     */
    void sign(Sign sign, Webkit webkit) throws Exception;


    /**
     * 获取所有的权限，可以用于同步后台数据库，方便操作
     *
     * @return
     */
    static List<RouterPermission> getRouterPermissions() {
        return RouterManager.getRouterPermissions();
    }
}
