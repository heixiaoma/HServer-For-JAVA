package top.hserver.core.ioc.ref;

import javassist.util.proxy.ProxyObject;
import top.hserver.cloud.CloudManager;
import top.hserver.cloud.bean.ClientData;
import top.hserver.cloud.proxy.CloudProxy;
import top.hserver.core.proxy.HookProxyFactory;
import top.hserver.core.queue.QueueDispatcher;
import top.hserver.core.interfaces.*;
import top.hserver.core.ioc.IocUtil;
import top.hserver.core.ioc.annotation.*;
import top.hserver.core.ioc.annotation.nacos.NacosValue;
import top.hserver.core.server.handlers.WebSocketServerHandler;
import top.hserver.core.server.router.RouterInfo;
import top.hserver.core.server.router.RouterManager;
import top.hserver.core.server.router.RouterPermission;
import top.hserver.core.server.util.ClassLoadUtil;
import top.hserver.core.server.util.ParameterUtil;
import top.hserver.core.server.util.PropUtil;
import top.hserver.core.task.TaskManager;
import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author hxm
 */
@Slf4j
public class InitBean {

    /**
     * 加载所有bean进容器
     */
    public static void init(Set<String> packageNames) {
        if (packageNames == null) {
            return;
        }
        packageNames.forEach(k -> {
            try {
                PackageScanner scan = new ClasspathPackageScanner(k);
                //读取配置文件
                initConfigurationProperties(scan);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        packageNames.forEach(k -> {
            try {
                PackageScanner scan = new ClasspathPackageScanner(k);
                initConfiguration(scan);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        packageNames.forEach(k -> {
            try {
                PackageScanner scan = new ClasspathPackageScanner(k);
                initWebSocket(scan);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        packageNames.forEach(k -> {
            try {
                PackageScanner scan = new ClasspathPackageScanner(k);
                initTest(scan);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        packageNames.forEach(k -> {
            try {
                PackageScanner scan = new ClasspathPackageScanner(k);
                initBean(scan);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        packageNames.forEach(k -> {
            try {
                PackageScanner scan = new ClasspathPackageScanner(k);
                initController(scan);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        packageNames.forEach(k -> {
            try {
                PackageScanner scan = new ClasspathPackageScanner(k);
                initHook(scan, packageNames);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        packageNames.forEach(k -> {
            try {
                PackageScanner scan = new ClasspathPackageScanner(k);
                QueueDispatcher.init(scan);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void initConfigurationProperties(PackageScanner scanner) throws Exception {
        //配置文件类自动装配
        List<Class<?>> clasps = scanner.getAnnotationList(ConfigurationProperties.class);
        for (Class<?> clasp : clasps) {
            String value = clasp.getAnnotation(ConfigurationProperties.class).prefix();
            if (value.trim().length() == 0) {
                value = null;
            }
            Object o = clasp.newInstance();
            for (Field field : clasp.getDeclaredFields()) {
                try {
                    PropUtil instance = PropUtil.getInstance();
                    String s = instance.get(value == null ? field.getName() : value + "." + field.getName(), null);
                    Object convert = ParameterUtil.convert(field.getType(), s);
                    if (convert != null) {
                        field.setAccessible(true);
                        field.set(o, convert);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                }

            }
            IocUtil.addBean(clasp.getName(), o);
        }

    }


    private static void initConfiguration(PackageScanner scan) throws Exception {
        List<Class<?>> clasps = scan.getAnnotationList(Configuration.class);
        for (Class aClass : clasps) {
            Method[] methods = aClass.getDeclaredMethods();
            Object o = aClass.newInstance();
            for (Field field : aClass.getDeclaredFields()) {
                //配置类只能注入字段和配置属性
                valuezr(field, o);
                //注入配置类
                zr(field, o);
                //注入Nacos配置中心的数据
                nacoszr(field, o);
            }
            for (Method method : methods) {
                Bean bean = method.getAnnotation(Bean.class);
                if (bean != null) {
                    try {
                        method.setAccessible(true);
                        Object invoke = method.invoke(o);
                        if (invoke != null) {
                            String value = bean.value();
                            if (value.trim().length() > 0) {
                                IocUtil.addBean(value, invoke);
                            } else {
                                IocUtil.addBean(invoke.getClass().getName(), invoke);
                            }
                        } else {
                            log.warn("{},方法返回空值，不进入容器", method.getName());
                        }
                    } catch (Exception e) {
                        log.warn("{},提示：方法不能有入参", method.getName());
                        log.warn(e.getMessage());
                    }
                }
            }
        }
    }

    private static void initWebSocket(PackageScanner scan) throws Exception {
        List<Class<?>> clasps = scan.getAnnotationList(WebSocket.class);
        for (Class aClass : clasps) {
            //检查注解里面是否有值
            WebSocket annotation = (WebSocket) aClass.getAnnotation(WebSocket.class);
            IocUtil.addBean(aClass.getName(), aClass.newInstance());
            WebSocketServerHandler.WebSocketRouter.put(annotation.value(), aClass.getName());
        }
    }

    private static void initTest(PackageScanner scan) throws Exception {
        try {
            Class<Annotation> aClass1 = (Class<Annotation>) InitBean.class.getClassLoader().loadClass("org.junit.runner.RunWith");
            List<Class<?>> clasps = scan.getAnnotationList(aClass1);
            for (Class aClass : clasps) {
                //检查注解里面是否有值
                IocUtil.addBean(aClass.getName(), aClass.newInstance());
            }
        } catch (Exception ignored) {
        }
    }


    /**
     * 初始化Bean
     */
    private static void initBean(PackageScanner scan) throws Exception {
        List<Class<?>> clasps = scan.getAnnotationList(Bean.class);
        for (Class aClass : clasps) {
            //检测这个Bean是否是全局异常处理的类
            if (GlobalException.class.isAssignableFrom(aClass)) {
                IocUtil.addListBean(GlobalException.class.getName(), aClass.newInstance());
                continue;
            }

            //检测这个Bean是否是初始化的类
            if (InitRunner.class.isAssignableFrom(aClass)) {
                IocUtil.addListBean(InitRunner.class.getName(), aClass.newInstance());
                continue;
            }

            //检测这个Bean是否是权限认证的
            if (PermissionAdapter.class.isAssignableFrom(aClass)) {
                IocUtil.addListBean(PermissionAdapter.class.getName(), aClass.newInstance());
                continue;
            }

            //检测这个Bean是否是track的
            if (TrackAdapter.class.isAssignableFrom(aClass)) {
                IocUtil.addListBean(TrackAdapter.class.getName(), aClass.newInstance());
                continue;
            }

            //检测这个Bean是否是track的
            if (FilterAdapter.class.isAssignableFrom(aClass)) {
                IocUtil.addListBean(FilterAdapter.class.getName(), aClass.newInstance());
                continue;
            }

            //检测这个Bean是否是response的
            if (ResponseAdapter.class.isAssignableFrom(aClass)) {
                IocUtil.addListBean(ResponseAdapter.class.getName(), aClass.newInstance());
                continue;
            }

            //检查注解里面是否有值
            Bean annotation = (Bean) aClass.getAnnotation(Bean.class);
            if (annotation.value().trim().length() > 0) {
                IocUtil.addBean(annotation.value(), aClass.newInstance());
            } else {
                IocUtil.addBean(aClass.getName(), aClass.newInstance());
            }

            //检测下Bean里面是否带又Task任务洛，带了就给他安排了
            Method[] methods = aClass.getDeclaredMethods();

            //检测当前的Bean是不是Rpc服务
            RpcService rpcService = (RpcService) aClass.getAnnotation(RpcService.class);
            //说明是rpc服务，单独存储一份她的数据哦
            if (rpcService != null) {
                if (rpcService.value().trim().length() > 0) {
                    //自定义了Rpc服务名
                    IocUtil.addBean(rpcService.value(), aClass.newInstance());
                } else {
                    //没有自定义服务名字
                    Class[] interfaces = aClass.getInterfaces();
                    if (interfaces != null && interfaces.length > 0) {
                        IocUtil.addBean(interfaces[0].getName(), aClass.newInstance());
                    } else {
                        log.error("RPC没有实现任何接口，预计调用过程会出现问题:{}", aClass.getSimpleName());
                    }
                }
            }

            for (Method method : methods) {
                Task task = method.getAnnotation(Task.class);
                if (task == null) {
                    continue;
                }
                if (annotation.value().trim().length() > 0) {
                    TaskManager.initTask(task.name(), task.time(), annotation.value(), method);
                } else {
                    TaskManager.initTask(task.name(), task.time(), aClass.getName(), method);
                }
            }

        }
    }

    /**
     * 初始化控制器
     */
    private static void initController(PackageScanner scan) throws Exception {
        /**
         * 检查是否有方法注解
         */
        List<Class<?>> clasps = scan.getAnnotationList(Controller.class);
        for (Class aClass : clasps) {
            //检查注解里面是否有值
            Method[] methods = aClass.getDeclaredMethods();
            for (Method method : methods) {
                Controller controller = (Controller) aClass.getAnnotation(Controller.class);
                String controllerPath = controller.value().trim();
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
                        routerInfo.setAClass(aClass);
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
                        routerInfo.setAClass(aClass);
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

    private static HookCheck checkHook(Class aClass1) {
        /**
         * 检查IOC是否存在aClass1对象
         * 遍历IOC,取出名字，将其替换为代理对象
         * Ioc存在单个Bean 和List集合类型
         */
        String iocName = null;
        boolean isList = false;
        Map<String, Object> allIoc = IocUtil.getAll();
        Iterator<String> iterator = allIoc.keySet().iterator();
        while (iterator.hasNext()) {
            String tempIocName = iterator.next();
            Object v = allIoc.get(tempIocName);
            if (v instanceof List) {
                List v1 = (List) v;
                for (Object o : v1) {
                    if (aClass1.isAssignableFrom(o.getClass())) {
                        iocName = tempIocName;
                        isList = true;
                        break;
                    }
                }
            } else {
                if (aClass1.isAssignableFrom(v.getClass()) && !ProxyObject.class.isAssignableFrom(v.getClass())) {
                    iocName = tempIocName;
                    break;
                }
            }
        }
        if (iocName == null) {
            return null;
        }
        return new HookCheck(iocName, isList);
    }


    private static void initHook(PackageScanner scan, Set<String> packages) throws Exception {
        HookProxyFactory hookProxyFactory = new HookProxyFactory();
        List<Class<?>> clasps = scan.getAnnotationList(Hook.class);
        for (Class aClass : clasps) {
            Hook hook = (Hook) aClass.getAnnotation(Hook.class);
            Class[] values = hook.value();
            for (Class value : values) {
                //Hook的是类注解.
                if (Annotation.class.isAssignableFrom(value)) {
                    for (String aPackage : packages) {
                        List<Class<?>> classes = ClassLoadUtil.LoadClasses(aPackage, false);
                        for (Class<?> aClass1 : classes) {
                            try {
                                Annotation annotation = aClass1.getAnnotation(value);
                                //看看类上面的注解是否有，
                                if (annotation != null) {
                                    Object newProxyInstance = hookProxyFactory.newProxyInstance(aClass1, value.getName() + "HOOK");
                                    if (newProxyInstance != null) {
                                        //不同名字，同样的bean 在引用不会出错
                                        IocUtil.addBean(newProxyInstance.getClass().getName(), newProxyInstance);
                                        HookCheck hookCheck = checkHook(aClass1);
                                        //说明是容器存在的，使用后将其替换
                                        if (hookCheck != null) {
                                            if (hookCheck.isList()) {
                                                IocUtil.addListBean(hookCheck.getIocName(), newProxyInstance);
                                            } else {
                                                IocUtil.addBean(hookCheck.getIocName(), newProxyInstance);
                                            }
                                        } else {
                                            IocUtil.addBean(aClass1.getName(), newProxyInstance);
                                        }
                                    }
                                } else {
                                    //方法级别的调用
                                    Method[] declaredMethods = aClass1.getDeclaredMethods();
                                    for (Method declaredMethod : declaredMethods) {
                                        Annotation annotation1 = declaredMethod.getAnnotation(value);
                                        if (annotation1 != null) {
                                            Object newProxyInstance = hookProxyFactory.newProxyInstance(aClass1, value.getName() + "HOOK");
                                            if (newProxyInstance != null) {
                                                IocUtil.addBean(newProxyInstance.getClass().getName(), newProxyInstance);
                                                HookCheck hookCheck = checkHook(aClass1);
                                                //说明是容器存在的，使用后将其替换
                                                if (hookCheck != null) {
                                                    if (hookCheck.isList()) {
                                                        IocUtil.addListBean(hookCheck.getIocName(), newProxyInstance);
                                                    } else {
                                                        IocUtil.addBean(hookCheck.getIocName(), newProxyInstance);
                                                    }
                                                } else {
                                                    IocUtil.addBean(aClass1.getName(), newProxyInstance);
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                            } catch (Throwable ignored) {
                            }
                        }
                    }
                } else {
                    //hook的普通方法
                    //检查容器是否有，没有重0生产 ,有就基于现在的进行生产
                    Object bean = IocUtil.getBean(value);
                    Object newProxyInstance;
                    if (bean != null) {
                        Class<?> aClass1 = bean.getClass();
                        newProxyInstance = hookProxyFactory.newProxyInstance(aClass1, value.getName() + "HOOK");
                    } else {
                        newProxyInstance = hookProxyFactory.newProxyInstance(value, value.getName() + "HOOK");
                    }
                    //将代理类放入容器，,一会让注入的时候就是代理类注入进去了
                    if (newProxyInstance != null) {
                        IocUtil.addBean(newProxyInstance.getClass().getName(), newProxyInstance);
                        IocUtil.addBean(value.getName(), newProxyInstance);
                    }
                }
                IocUtil.addListBean(value.getName() + "HOOK", aClass.newInstance());
            }
        }
    }

    /**
     * 给所有Bean和Filter分配依赖(自动装配)
     */
    public static void injection() {
        //Bean对象
        Map<String, Object> all = IocUtil.getAll();
        all.forEach((k, v) -> {
            //注意有一个List类型的IOC
            if (v instanceof List) {
                List v1 = (List) v;
                for (Object o : v1) {
                    autoZr(o);
                }
            } else {
                autoZr(v);
            }

        });
    }


    private static void autoZr(Object v) {
        Class par = v.getClass();
        while (!par.equals(Object.class)) {
            //获取当前类的所有字段
            Field[] declaredFields = par.getDeclaredFields();
            for (Field field : declaredFields) {
                //Value 注入
                valuezr(field, v);
                //Autowired注入
                zr(field, v);
                //rpc注入
                rpczr(field, v);
                //nacos注入
                nacoszr(field, v);

            }
            par = par.getSuperclass();
        }
    }


    /**
     * Nacos注入
     *
     * @param declaredField
     * @param v
     */
    private static void nacoszr(Field declaredField, Object v) {
        NacosValue annotation = declaredField.getAnnotation(NacosValue.class);
        if (annotation != null) {
            try {
                declaredField.setAccessible(true);
                Object bean = IocUtil.getBean(annotation.dataId() + annotation.group());
                if (bean == null) {
                    log.info("{}：空值", v.getClass().getSimpleName());
                    return;
                }
                declaredField.set(v, bean);
                log.info("{}----->{}：装配完成，NacosValue装配", bean.getClass().getSimpleName(), v.getClass().getSimpleName());
            } catch (Exception e) {
                log.error("{}----->{}：NacosValue装配失败", v.getClass().getSimpleName(), v.getClass().getSimpleName());
            }
        }
    }


    /**
     * Rpc 服务的代理对象生成
     */
    private static void rpczr(Field declaredField, Object v) {
        Resource annotation = declaredField.getAnnotation(Resource.class);
        if (annotation != null) {
            try {
                CloudManager.add(annotation.serverName());
                declaredField.setAccessible(true);
                Object proxy = CloudProxy.getProxy(declaredField.getType(), annotation);
                declaredField.set(v, proxy);
                log.info("{}----->{}：装配完成，Rpc装配", proxy.getClass().getSimpleName(), v.getClass().getSimpleName());
            } catch (Exception e) {
                log.error("{}----->{}：装配错误:RPC代理生成失败", v.getClass().getSimpleName(), v.getClass().getSimpleName());
            }
        }
    }


    /**
     * 自动配置类里面，普通Bean，配置Props的
     *
     * @param declaredField
     * @param v
     */
    private static void valuezr(Field declaredField, Object v) {
        Value annotation = declaredField.getAnnotation(Value.class);
        if (annotation != null) {
            try {
                declaredField.setAccessible(true);
                PropUtil instance = PropUtil.getInstance();
                String s = instance.get(annotation.value());
                Object convert = ParameterUtil.convert(declaredField.getType(), s);
                declaredField.set(v, convert);
            } catch (Exception e) {
                log.error("{}----->{}：@Value装配错误", v.getClass().getSimpleName(), v.getClass().getSimpleName());
            }
        }
    }


    private static void zr(Field declaredField, Object v) {
        //检查是否有注解@Autowired
        Autowired annotation = declaredField.getAnnotation(Autowired.class);
        if (annotation != null) {
            declaredField.setAccessible(true);
            String findMsg;
            Object bean;
            if (annotation.value().trim().length() > 0) {
                bean = IocUtil.getBean(annotation.value());
                findMsg = "按自定义名字装配，" + declaredField.getType().getSimpleName();
            } else {
                findMsg = "按类型装配，" + declaredField.getType().getSimpleName();
                bean = IocUtil.getBean(declaredField.getType());
            }
            if (bean == null) {
                Map<String, Object> all = IocUtil.getAll();
                List<Class> allClassByInterface = new ArrayList<>();
                //获取是否是子类对象，如果是也可以装配
                all.forEach((a, b) -> {
                    if (declaredField.getType().isAssignableFrom(b.getClass())) {
                        allClassByInterface.add(b.getClass());
                    }
                });
                if (allClassByInterface.size() > 0) {
                    if (allClassByInterface.size() > 1) {
                        int tempCode = 0;
                        boolean flag = false;
                        for (Class aClass : allClassByInterface) {
                            if (tempCode == 0) {
                                tempCode = IocUtil.getBean(aClass).hashCode();
                            } else {
                                if (tempCode != IocUtil.getBean(aClass).hashCode()) {
                                    flag = true;
                                    break;
                                }
                            }
                        }
                        if (flag) {
                            log.warn("装配警告，存在多个子类，建议通过Bean名字装配，避免装配错误");
                        }
                    }
                    bean = IocUtil.getBean(allClassByInterface.get(0));
                    findMsg = "按子类装配，" + declaredField.getType().getSimpleName();
                }
            }

            if (bean == null) {
                return;
            }

            try {
                //同类型注入
                if (bean.getClass().getName().contains(declaredField.getType().getName())) {
                    declaredField.set(v, bean);
                    log.info("{}----->{}：装配完成，{}", bean.getClass().getSimpleName(), v.getClass().getSimpleName(), findMsg);
                    //父类检测注入
                } else if (declaredField.getType().isAssignableFrom(bean.getClass())) {
                    declaredField.set(v, bean);
                    log.info("{}----->{}：装配完成，{}", bean.getClass().getSimpleName(), v.getClass().getSimpleName(), findMsg);
                } else {
                    log.error("{}----->{}：装配错误:类型不匹配", v.getClass().getSimpleName(), v.getClass().getSimpleName());
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("装配错误:{},{}", declaredField.getName(), e.getMessage());
            }
        }
    }


}
