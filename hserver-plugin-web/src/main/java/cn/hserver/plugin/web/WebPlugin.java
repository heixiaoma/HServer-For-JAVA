package cn.hserver.plugin.web;

import cn.hserver.core.interfaces.PluginAdapter;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.ref.PackageScanner;
import cn.hserver.core.log.HServerPatternLayout;
import cn.hserver.core.server.util.ExceptionUtil;
import cn.hserver.core.server.util.PropUtil;
import cn.hserver.core.server.util.TTLUtil;
import cn.hserver.plugin.web.context.WebConstConfig;
import cn.hserver.plugin.web.util.ParameterUtil;
import cn.hserver.plugin.web.annotation.*;
import cn.hserver.plugin.web.handlers.WebSocketServerHandler;
import cn.hserver.plugin.web.interfaces.*;
import cn.hserver.plugin.web.log.RequestIdClassicConverter;
import cn.hserver.plugin.web.router.RouterInfo;
import cn.hserver.plugin.web.router.RouterManager;
import cn.hserver.plugin.web.router.RouterPermission;
import cn.hserver.plugin.web.util.SslContextUtil;
import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

public class WebPlugin implements PluginAdapter {

    private static final Logger log = LoggerFactory.getLogger(WebPlugin.class);


    @Override
    public void startApp() {
        //日志初始化
        HServerPatternLayout.defaultConverterMap.put("requestId", RequestIdClassicConverter.class.getName());
    }

    @Override
    public void startIocInit() {
        PropUtil instance = PropUtil.getInstance();
        //配置文件初始化
        if (instance.get("web.readLimit").trim().length() > 0) {
            WebConstConfig.READ_LIMIT = Long.valueOf(instance.get("web.readLimit"));
        }
        if (instance.get("web.rootPath").trim().length() > 0) {
            WebConstConfig.ROOT_PATH = instance.get("web.rootPath");
        }
        if (instance.get("web.writeLimit").trim().length() > 0) {
            WebConstConfig.WRITE_LIMIT = Long.valueOf(instance.get("web.writeLimit"));
        }
        if (instance.get("web.httpContentSize").trim().length() > 0) {
            WebConstConfig.HTTP_CONTENT_SIZE = instance.getInt("web.httpContentSize");
        }
        Integer businessPool = instance.getInt("web.businessPool");
        if (businessPool != null && businessPool > 0) {
            WebConstConfig.BUSINESS_EVENT = TTLUtil.getEventLoop(businessPool, "hserver_business");
        }
        if (businessPool != null && businessPool < 0) {
            WebConstConfig.BUSINESS_EVENT = null;
        } else {
            WebConstConfig.BUSINESS_EVENT = TTLUtil.getEventLoop(50, "hserver_business");
        }
    }

    @Override
    public boolean iocInitBean(Class aClass) {
        try {
            //检测这个Bean是否是全局异常处理的类
            if (GlobalException.class.isAssignableFrom(aClass)) {
                IocUtil.addListBean(GlobalException.class.getName(), aClass.newInstance());
                return true;
            }

            //检测这个Bean是否是权限认证的
            if (PermissionAdapter.class.isAssignableFrom(aClass)) {
                IocUtil.addListBean(PermissionAdapter.class.getName(), aClass.newInstance());
                return true;

            }

            //检测这个Bean是否是FilterAdapter的
            if (FilterAdapter.class.isAssignableFrom(aClass)) {
                IocUtil.addListBean(FilterAdapter.class.getName(), aClass.newInstance());
                return true;

            }

            //检测这个Bean是否是LimitAdapter的
            if (LimitAdapter.class.isAssignableFrom(aClass)) {
                IocUtil.addListBean(LimitAdapter.class.getName(), aClass.newInstance());
                return true;

            }

            //检测这个Bean是否是response的
            if (ResponseAdapter.class.isAssignableFrom(aClass)) {
                IocUtil.addListBean(ResponseAdapter.class.getName(), aClass.newInstance());
                return true;

            }
        } catch (Exception e) {
            log.error(ExceptionUtil.getMessage(e));
        }
        return false;

    }

    @Override
    public void iocInit(PackageScanner scan) {
        try {
            //初始化Websocket
            initWebSocket(scan);
        } catch (Exception e) {
            log.error(ExceptionUtil.getMessage(e));
        }


        try {
            //初始化控制器
            initController(scan);
        } catch (Exception e) {
            log.error(ExceptionUtil.getMessage(e));
        }

    }

    @Override
    public void iocInitEnd() {

    }

    @Override
    public void startInjection() {

    }

    @Override
    public void injectionEnd() {
        SslContextUtil.setSsl();
    }

    private static void initWebSocket(PackageScanner scan) throws Exception {
        Set<Class<?>> clasps = scan.getAnnotationList(WebSocket.class);
        for (Class aClass : clasps) {
            //检查注解里面是否有值
            WebSocket annotation = (WebSocket) aClass.getAnnotation(WebSocket.class);
            IocUtil.addBean(aClass.getName(), aClass.newInstance());
            WebSocketServerHandler.WEB_SOCKET_ROUTER.put(annotation.value(), aClass.getName());
        }
    }

    /**
     * 初始化控制器
     */
    private static void initController(PackageScanner scan) throws Exception {
        /**
         * 检查是否有方法注解
         */
        Set<Class<?>> clasps = scan.getAnnotationList(Controller.class);
        for (Class aClass : clasps) {
            //检查注解里面是否有值
            Method[] methods = aClass.getDeclaredMethods();
            for (Method method : methods) {
                Controller controller = (Controller) aClass.getAnnotation(Controller.class);
                String controllerPath = WebConstConfig.ROOT_PATH.trim() + controller.value().trim();
                /**
                 * 这里对方法控制器的注解的方法参数，进行初始化
                 * 非控制器的方法过滤排除
                 */
                Annotation[] annotations = method.getAnnotations();
                if (Arrays.stream(annotations).noneMatch(annotation -> annotation.annotationType().getAnnotation(Request.class) != null)) {
                    continue;
                }
                try {
                    ParameterUtil.addParam(aClass, method);
                } catch (Exception ignored) {
                    continue;
                }
                //细化后的注解
                Class[] classes = new Class[]{GET.class, POST.class, HEAD.class, PUT.class, PATCH.class, DELETE.class, OPTIONS.class, CONNECT.class, TRACE.class};
                for (Class aClass1 : classes) {
                    Annotation annotation = method.getAnnotation(aClass1);
                    if (annotation != null) {
                        Method value = aClass1.getMethod("value");
                        value.setAccessible(true);
                        String path = controllerPath + value.invoke(annotation).toString();
                        RouterInfo routerInfo = new RouterInfo();
                        method.setAccessible(true);
                        routerInfo.setMethod(method);
                        routerInfo.setaClass(aClass);
                        routerInfo.setUrl(path);
                        routerInfo.setReqMethodName(HttpMethod.valueOf(aClass1.getSimpleName()));
                        RouterManager.addRouter(routerInfo);
                        //检查权限
                        Sign sign = method.getAnnotation(Sign.class);
                        RequiresRoles requiresRoles = method.getAnnotation(RequiresRoles.class);
                        RequiresPermissions requiresPermissions = method.getAnnotation(RequiresPermissions.class);
                        //有一个不为空都存一次
                        if (sign != null || requiresRoles != null || requiresPermissions != null) {
                            RouterPermission routerPermission = new RouterPermission();
                            routerPermission.setUrl(path);
                            routerPermission.setReqMethodName(HttpMethod.valueOf(aClass1.getSimpleName()));
                            routerPermission.setSign(sign);
                            routerPermission.setRequiresRoles(requiresRoles);
                            routerPermission.setRequiresPermissions(requiresPermissions);
                            routerPermission.setControllerPackageName(aClass.getName());
                            routerPermission.setControllerName(controller.name().trim());
                            RouterManager.addPermission(routerPermission);
                        }
                    }
                }
                //通用版注解
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                if (requestMapping != null) {
                    RequestMethod[] requestMethods = requestMapping.method();
                    String[] requestMethod;
                    if (requestMethods.length == 0) {
                        requestMethod = RequestMethod.getRequestMethodAll();
                    } else {
                        String[] rm = new String[requestMethods.length];
                        for (int i = 0; i < requestMethods.length; i++) {
                            rm[i] = requestMethods[i].name();
                        }
                        requestMethod = rm;
                    }
                    for (String s : requestMethod) {
                        String path = controllerPath + requestMapping.value();
                        RouterInfo routerInfo = new RouterInfo();
                        method.setAccessible(true);
                        routerInfo.setMethod(method);
                        routerInfo.setUrl(path);
                        routerInfo.setaClass(aClass);
                        routerInfo.setReqMethodName(HttpMethod.valueOf(s));
                        RouterManager.addRouter(routerInfo);
                        //检查权限
                        Sign sign = method.getAnnotation(Sign.class);
                        RequiresRoles requiresRoles = method.getAnnotation(RequiresRoles.class);
                        RequiresPermissions requiresPermissions = method.getAnnotation(RequiresPermissions.class);
                        //有一个不为空都存一次
                        if (sign != null || requiresRoles != null || requiresPermissions != null) {
                            RouterPermission routerPermission = new RouterPermission();
                            routerPermission.setUrl(path);
                            routerPermission.setReqMethodName(HttpMethod.valueOf(s));
                            routerPermission.setSign(sign);
                            routerPermission.setRequiresRoles(requiresRoles);
                            routerPermission.setRequiresPermissions(requiresPermissions);
                            routerPermission.setControllerPackageName(aClass.getName());
                            routerPermission.setControllerName(controller.name().trim());
                            RouterManager.addPermission(routerPermission);
                        }
                    }
                }
            }
            IocUtil.addBean(aClass.getName(), aClass.newInstance());
        }
    }


}
