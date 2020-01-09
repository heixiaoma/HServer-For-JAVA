package top.hserver.core.ioc.ref;

import top.hserver.cloud.CloudManager;
import top.hserver.cloud.bean.ClientData;
import top.hserver.cloud.proxy.CloudProxy;
import top.hserver.core.interfaces.GlobalException;
import top.hserver.core.interfaces.InitRunner;
import top.hserver.core.interfaces.PermissionAdapter;
import top.hserver.core.ioc.IocUtil;
import top.hserver.core.interfaces.FilterAdapter;
import top.hserver.core.ioc.annotation.*;
import top.hserver.core.ioc.util.ClassLoadUtil;
import top.hserver.core.proxy.JavassistProxyFactory;
import top.hserver.core.server.filter.FilterChain;
import top.hserver.core.server.handlers.WebSocketServerHandler;
import top.hserver.core.server.router.RouterInfo;
import top.hserver.core.server.router.RouterManager;
import top.hserver.core.server.router.RouterPermission;
import top.hserver.core.server.util.ParameterUtil;
import top.hserver.core.task.TaskManager;
import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public class InitBean {

    /**
     * 加载所有bean进容器
     *
     * @param baseClass
     */
    public static void init(Class<?> baseClass) {
        try {
            if (baseClass == null || baseClass.getPackage() == null || baseClass.getPackage().getName() == null) {
                return;
            }
            PackageScanner scan = new ClasspathPackageScanner(baseClass.getPackage().getName());
            //读取配置文件
            initConfiguration(scan);
            initWebSocket(scan);
            initBean(scan);
            initController(scan);
            initHook(scan);
            initFilter(scan);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void initConfiguration(PackageScanner scan) throws Exception {
        List<Class<?>> classs = scan.getConfigurationPackage();
        for (Class aClass : classs) {
            //检查注解里面是否有值
            Configuration annotation = (Configuration) aClass.getAnnotation(Configuration.class);
            if (annotation != null) {
                Method[] methods = aClass.getDeclaredMethods();
                Object o = aClass.newInstance();
                for (Method method : methods) {
                    Bean bean = method.getAnnotation(Bean.class);
                    if (bean != null) {
                        Object invoke = method.invoke(o);
                        String value = bean.value();
                        if (value.trim().length() > 0) {
                            IocUtil.addBean(value, invoke);
                        } else {
                            IocUtil.addBean(invoke.getClass().getName(), invoke);
                        }
                    }
                }
            }
        }
    }


    private static void initWebSocket(PackageScanner scan) throws Exception {
        List<Class<?>> classs = scan.getWebSocketPackage();
        for (Class aClass : classs) {
            //检查注解里面是否有值
            WebSocket annotation = (WebSocket) aClass.getAnnotation(WebSocket.class);
            if (annotation != null) {
                IocUtil.addBean(aClass.getName(), aClass.newInstance());
                WebSocketServerHandler.WebSocketRouter.put(annotation.value(), aClass.getName());
            }
        }
    }

    /**
     * 初始化Bean
     */
    private static void initBean(PackageScanner scan) throws Exception {
        List<Class<?>> classs = scan.getBeansPackage();
        for (Class aClass : classs) {

            //检测这个Bean是否是全局异常处理的类
            if (GlobalException.class.isAssignableFrom(aClass)) {
                IocUtil.addBean(GlobalException.class.getName(), aClass.newInstance());
                continue;
            }

            //检测这个Bean是否是初始化的类
            if (InitRunner.class.isAssignableFrom(aClass)) {
                IocUtil.addBean(InitRunner.class.getName(), aClass.newInstance());
                continue;
            }

            //检测这个Bean是否是权限认证的
            if (PermissionAdapter.class.isAssignableFrom(aClass)) {
                IocUtil.addBean(PermissionAdapter.class.getName(), aClass.newInstance());
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
                ClientData clientData = new ClientData();
                clientData.setClassName(aClass.getName());
                clientData.setMethods(methods);
                if (rpcService.value().trim().length() > 0) {
                    //自定义了Rpc服务名
                    clientData.setAClass(rpcService.value());
                    CloudManager.add(rpcService.value(), clientData);
                    IocUtil.addBean(rpcService.value(), aClass.newInstance());
                } else {
                    //没有自定义服务名字
                    Class[] interfaces = aClass.getInterfaces();
                    if (interfaces != null && interfaces.length > 0) {
                        clientData.setAClass(interfaces[0].getName());
                        IocUtil.addBean(interfaces[0].getName(), aClass.newInstance());
                        CloudManager.add(interfaces[0].getName(), clientData);
                    } else {
                        log.error("RPC没有实现任何接口，预计调用过程会出现问题:" + aClass.getSimpleName());
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
        List<Class<?>> classs = scan.getControllersPackage();
        for (Class aClass : classs) {
            //检查注解里面是否有值
            Method[] methods = aClass.getDeclaredMethods();
            for (Method method : methods) {
                /**
                 * 这里对方法控制器的注解的方法参数，进行初始化
                 */
                ParameterUtil.addParam(aClass, method);
                GET get = method.getAnnotation(GET.class);
                POST post = method.getAnnotation(POST.class);
                if (get != null) {
                    RouterInfo routerInfo = new RouterInfo();
                    routerInfo.setMethod(method);
                    routerInfo.setUrl(get.value());
                    routerInfo.setAClass(aClass);
                    routerInfo.setReqMethodName(HttpMethod.GET);
                    RouterManager.addRouter(routerInfo);
                    //检查权限
                    Sign sign = method.getAnnotation(Sign.class);
                    RequiresRoles requiresRoles = method.getAnnotation(RequiresRoles.class);
                    RequiresPermissions requiresPermissions = method.getAnnotation(RequiresPermissions.class);
                    //有一个不为空都存一次
                    if (sign!=null||requiresRoles!=null||requiresPermissions!=null){
                        RouterPermission routerPermission =new RouterPermission();
                        routerPermission.setUrl(get.value());
                        routerPermission.setReqMethodName(HttpMethod.GET);
                        routerPermission.setSign(sign);
                        routerPermission.setRequiresRoles(requiresRoles);
                        routerPermission.setRequiresPermissions(requiresPermissions);
                        RouterManager.addPermission(routerPermission);
                    }
                }
                if (post != null) {
                    RouterInfo routerInfo = new RouterInfo();
                    routerInfo.setMethod(method);
                    routerInfo.setUrl(post.value());
                    routerInfo.setAClass(aClass);
                    routerInfo.setReqMethodName(HttpMethod.POST);
                    RouterManager.addRouter(routerInfo);
                    //检查权限
                    Sign sign = method.getAnnotation(Sign.class);
                    RequiresRoles requiresRoles = method.getAnnotation(RequiresRoles.class);
                    RequiresPermissions requiresPermissions = method.getAnnotation(RequiresPermissions.class);
                    //有一个不为空都存一次
                    if (sign!=null||requiresRoles!=null||requiresPermissions!=null){
                        RouterPermission routerPermission =new RouterPermission();
                        routerPermission.setUrl(post.value());
                        routerPermission.setReqMethodName(HttpMethod.POST);
                        routerPermission.setSign(sign);
                        routerPermission.setRequiresRoles(requiresRoles);
                        routerPermission.setRequiresPermissions(requiresPermissions);
                        RouterManager.addPermission(routerPermission);
                    }
                }
            }
            IocUtil.addBean(aClass.getName(), aClass.newInstance());
        }
    }


    /**
     * 处理下Filter的加载，放在Ioc容器
     */
    private static void initFilter(PackageScanner scan) throws Exception {
        List<Class<?>> classes = scan.getFiltersPackage();
        // 载入事件处理类
        Map<Integer, Map<String, FilterAdapter>> map = new HashMap<>();
        int tempMax = 0;
        // 解析事件处理类
        for (Class<?> clazz : classes) {
            Filter handlerAnno = clazz.getAnnotation(Filter.class);
            if (handlerAnno == null) {
                continue;
            }
            log.info(clazz.getCanonicalName() + "优先级：" + handlerAnno.value());
            FilterAdapter obj = null;
            try {
                obj = (FilterAdapter) clazz.newInstance();
            } catch (Exception e) {
                log.error("初始化 " + clazz.getSimpleName() + " 错误", e);
                continue;
            }
            if (obj != null) {
                if (map.containsKey(handlerAnno.value())) {
                    throw new RuntimeException("初始化插件出现异常，顺序冲突:" + handlerAnno.value());
                } else {
                    Map<String, FilterAdapter> filterMap = new HashMap<>();
                    filterMap.put(clazz.getName(), obj);
                    map.put(handlerAnno.value(), filterMap);
                    if (handlerAnno.value() > tempMax) {
                        tempMax = handlerAnno.value();
                    }
                }
            }
        }
        for (int i = 0; i <= tempMax; i++) {
            if (map.containsKey(i)) {
                Map<String, FilterAdapter> filterMap = map.get(i);
                FilterChain.filtersIoc.add(filterMap);
            }
        }
    }


    private static void initHook(PackageScanner scan) throws Exception {
        JavassistProxyFactory javassistProxyFactory = new JavassistProxyFactory();
        List<Class<?>> classs = scan.getHooksPackage();
        for (Class aClass : classs) {
            Hook hook = (Hook) aClass.getAnnotation(Hook.class);
            Class value = hook.value();
            String method = hook.method();
            Object newProxyInstance = javassistProxyFactory.newProxyInstance(value, aClass.getName(), method);
            //将Hook实例类放在容器里面，一会儿还需要检查他是否有其他数据，需要注入
            IocUtil.addBean(aClass.getName(), aClass.newInstance());
            //将代理类放入容器，一会儿不能把自己替换了
            IocUtil.addBean(value.getName(), newProxyInstance);
        }
    }

    /**
     * 给所有Bean和Filter分配依赖(自动装配)
     */
    public static void injection() {

        //Bean对象
        Map<String, Object> all = IocUtil.getAll();
        all.forEach((k, v) -> {
            //获取当前类的所有字段
            Field[] declaredFields = v.getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                zr(declaredField, v);
                rpczr(declaredField, v);
            }
            //aop的代理对象，检查一次
            Field[] declaredFields1 = v.getClass().getSuperclass().getDeclaredFields();
            for (Field field : declaredFields1) {
                zr(field, v);
                rpczr(field, v);
            }
        });

        //Filter注入
        List<Map<String, FilterAdapter>> filtersIoc = FilterChain.filtersIoc;
        filtersIoc.forEach((v) -> {
            //获取当前类的所有字段
            String next = v.keySet().iterator().next();
            FilterAdapter filterAdapter = v.get(next);
            Field[] declaredFields = filterAdapter.getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                zr(declaredField, filterAdapter);
                rpczr(declaredField, filterAdapter);
            }
            //aop的代理对象，检查一次
            Field[] declaredFields1 = filterAdapter.getClass().getSuperclass().getDeclaredFields();
            for (Field field : declaredFields1) {
                zr(field, filterAdapter);
                rpczr(field, filterAdapter);
            }
        });
    }


    /**
     * Rpc 服务的代理对象生成
     */
    private static void rpczr(Field declaredField, Object v) {
        Resource annotation = declaredField.getAnnotation(Resource.class);
        if (annotation != null) {
            try {
                declaredField.setAccessible(true);
                Object proxy = CloudProxy.getProxy(declaredField.getType(), annotation);
                declaredField.set(v, proxy);
                log.info(proxy.getClass().getSimpleName() + "----->" + v.getClass().getSimpleName() + "：装配完成，Rpc装配");
            } catch (Exception e) {
                log.error(v.getClass().getSimpleName() + "----->" + v.getClass().getSimpleName() + "：装配错误:RPC代理生成失败");
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
                        log.warn("装配警告，存在多个子类，建议通过Bean名字装配，避免装配错误");
                    }
                    bean = IocUtil.getBean(allClassByInterface.get(0));
                    findMsg = "按子类装配，" + declaredField.getType().getSimpleName();
                } else {
                    log.error("装配错误:容器中未找到对应的Bean对象装备配,查找说明：" + findMsg);
                    return;
                }
            }
            try {
                //同类型注入
                if (bean.getClass().getName().contains(declaredField.getType().getName())) {
                    declaredField.set(v, bean);
                    log.info(bean.getClass().getSimpleName() + "----->" + v.getClass().getSimpleName() + "：装配完成，" + findMsg);
                    //父类检测注入
                } else if (declaredField.getType().isAssignableFrom(bean.getClass())) {
                    declaredField.set(v, bean);
                    log.info(bean.getClass().getSimpleName() + "----->" + v.getClass().getSimpleName() + "：装配完成，" + findMsg);
                } else {
                    log.error(v.getClass().getSimpleName() + "----->" + v.getClass().getSimpleName() + "：装配错误:类型不匹配");
                }
            } catch (Exception e) {
                log.error("装配错误:" + e.getMessage());
            }
        }
    }

}