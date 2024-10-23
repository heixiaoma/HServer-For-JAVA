package cn.hserver.plugin.web.handlers.check;

import cn.hserver.core.ioc.IocUtil;
import cn.hserver.plugin.web.context.HServerContext;
import cn.hserver.plugin.web.context.HServerContextHolder;
import cn.hserver.plugin.web.exception.BusinessException;
import cn.hserver.plugin.web.interfaces.PermissionAdapter;
import cn.hserver.plugin.web.router.RouterManager;
import cn.hserver.plugin.web.router.RouterPermission;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Permission implements DispatcherHandler{

    private static final Logger log = LoggerFactory.getLogger(Permission.class);

    //记录是否有权限对象
    private boolean hasPermission = true;
    List<PermissionAdapter> listBean = IocUtil.getListBean(PermissionAdapter.class);

    @Override
    public HServerContext dispatcher(HServerContext hServerContext) {
        //如果是静态文件就不进行权限验证了,或者是没有权限对象得
        if (hServerContext.isStaticFile()||!hasPermission) {
            return hServerContext;
        }
        /**
         * 检查下Webkit是否设置了值
         *  文本输出
         *  下载
         *  重定向
         *
         */
        if (hServerContext.getWebkit().httpResponse.hasData()) {
            return hServerContext;
        }
        if (listBean != null) {
            RouterPermission routerPermission = RouterManager.getRouterPermission(hServerContext.getRequest().getUri(), hServerContext.getRequest().getRequestType());
            if (routerPermission != null) {
                for (PermissionAdapter permissionAdapter : listBean) {
                    if (routerPermission.getRequiresPermissions() != null) {
                        try {
                            permissionAdapter.requiresPermissions(routerPermission.getRequiresPermissions(), hServerContext.getWebkit());
                            if (hServerContext.getWebkit().httpResponse.hasData()) {
                                break;
                            }
                        } catch (Exception e) {
                            throw new BusinessException(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "权限验证", e, hServerContext.getWebkit());
                        }
                    }
                    if (routerPermission.getRequiresRoles() != null) {
                        try {
                            permissionAdapter.requiresRoles(routerPermission.getRequiresRoles(), hServerContext.getWebkit());
                            if (hServerContext.getWebkit().httpResponse.hasData()) {
                                break;
                            }
                        } catch (Exception e) {
                            throw new BusinessException(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "角色验证", e, hServerContext.getWebkit());
                        }
                    }
                    if (routerPermission.getSign() != null) {
                        try {
                            permissionAdapter.sign(routerPermission.getSign(), hServerContext.getWebkit());
                            if (hServerContext.getWebkit().httpResponse.hasData()) {
                                break;
                            }
                        } catch (Exception e) {
                            throw new BusinessException(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "Sign验证", e, hServerContext.getWebkit());
                        }
                    }
                }
            }
        }else {
            hasPermission=false;
        }
        return hServerContext;
    }
}
