package top.hserver.core.ioc.ref;

import top.hserver.cloud.CloudManager;
import top.hserver.cloud.bean.ClientData;
import top.hserver.cloud.proxy.CloudProxy;
import top.hserver.core.event.EventDispatcher;
import top.hserver.core.interfaces.*;
import top.hserver.core.ioc.IocUtil;
import top.hserver.core.ioc.annotation.*;
import top.hserver.core.proxy.JavassistProxyFactory;
import top.hserver.core.server.filter.FilterChain;
import top.hserver.core.server.handlers.WebSocketServerHandler;
import top.hserver.core.server.router.RouterInfo;
import top.hserver.core.server.router.RouterManager;
import top.hserver.core.server.router.RouterPermission;
import top.hserver.core.server.util.ParameterUtil;
import top.hserver.core.server.util.PropUtil;
import top.hserver.core.task.TaskManager;
import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
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
            initTest(scan);
            initBean(scan);
            initController(scan);
            initHook(scan);
            initFilter(scan);
            //初始化异步事件
            EventDispatcher.init(scan);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void initConfiguration(PackageScanner scan) throws Exception {
        List<Class<?>> classs = scan.getAnnotationList(Configuration.class);
        for (Class aClass : classs) {
            Method[] methods = aClass.getDeclaredMethods();
            Object o = aClass.newInstance();
            for (Field field : aClass.getDeclaredFields()) {
              valuezr(field,o);
            }
            for (Method method : methods) {
                Bean bean = method.getAnnotation(Bean.class);
                if (bean != null) {
                    try {
                      Object invoke = method.invoke(o);
                      if (invoke!=null){
                        String value = bean.value();
                        if (value.trim().length() > 0) {
                          IocUtil.addBean(value, invoke);
                        } else {
                          IocUtil.addBean(invoke.getClass().getName(), invoke);
                        }
                      }else {
                        log.warn("{},方法返回空值，不进入容器",method.getName());
                      }
                    }catch (Exception e){
                      log.warn("{},方法不能有入参",method.getName());
                    }
                }
            }
        }
    }

    private static void initWebSocket(PackageScanner scan) throws Exception {
        List<Class<?>> classs = scan.getAnnotationList(WebSocket.class);
        for (Class aClass : classs) {
            //检查注解里面是否有值
            WebSocket annotation = (WebSocket) aClass.getAnnotation(WebSocket.class);
            IocUtil.addBean(aClass.getName(), aClass.newInstance());
            WebSocketServerHandler.WebSocketRouter.put(annotation.value(), aClass.getName());
        }
    }

    private static void initTest(PackageScanner scan) throws Exception {
        try {
            Class<Annotation> aClass1 = (Class<Annotation>) InitBean.class.getClassLoader().loadClass("org.junit.runner.RunWith");
            List<Class<?>> classs = scan.getAnnotationList(aClass1);
            for (Class aClass : classs) {
                //检查注解里面是否有值
                IocUtil.addBean(aClass.getName(), aClass.newInstance());
            }
        } catch (Exception e) {
        }
    }

    /**
     * 初始化Bean
     */
    private static void initBean(PackageScanner scan) throws Exception {
        List<Class<?>> classs = scan.getAnnotationList(Bean.class);
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

            //检测这个Bean是否是track的
            if (TrackAdapter.class.isAssignableFrom(aClass)) {
                IocUtil.addBean(TrackAdapter.class.getName(), aClass.newInstance());
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
        List<Class<?>> classs = scan.getAnnotationList(Controller.class);
        for (Class aClass : classs) {
            //检查注解里面是否有值
            Method[] methods = aClass.getDeclaredMethods();
            for (Method method : methods) {
                Controller controller = (Controller) aClass.getAnnotation(Controller.class);
                String controllerPath = controller.value().trim();
                /**
                 * 这里对方法控制器的注解的方法参数，进行初始化
                 */
                ParameterUtil.addParam(aClass, method);
                //细化后的注解
                Class[] classes = new Class[]{GET.class, POST.class, HEAD.class, PUT.class, PATCH.class, DELETE.class, OPTIONS.class, CONNECT.class, TRACE.class};
                for (Class aClass1 : classes) {
                    Annotation annotation = method.getAnnotation(aClass1);
                    if (annotation != null) {
                        Method value = aClass1.getMethod("value");
                        String path = controllerPath + value.invoke(annotation).toString();
                        RouterInfo routerInfo = new RouterInfo();
                        routerInfo.setMethod(method);
                        routerInfo.setUrl(path);
                        routerInfo.setAClass(aClass);
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
                    if (requestMethods == null || requestMethods.length == 0) {
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


    /**
     * 处理下Filter的加载，放在Ioc容器
     */
    private static void initFilter(PackageScanner scan) throws Exception {
        List<Class<?>> classes = scan.getAnnotationList(Filter.class);
        // 载入事件处理类
        Map<Integer, Map<String, FilterAdapter>> map = new HashMap<>();
        int tempMax = 0;
        // 解析事件处理类
        for (Class<?> clazz : classes) {
            Filter handlerAnno = clazz.getAnnotation(Filter.class);
            if (handlerAnno == null) {
                continue;
            }
            log.info("{}优先级：{}", clazz.getCanonicalName(), handlerAnno.value());
            FilterAdapter obj = null;
            try {
                obj = (FilterAdapter) clazz.newInstance();
            } catch (Exception e) {
                log.error("初始化 {} 错误:{}", clazz.getSimpleName(), e);
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
        List<Class<?>> classs = scan.getAnnotationList(Hook.class);
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
                valuezr(declaredField, v);
                zr(declaredField, v);
                rpczr(declaredField, v);
            }
            //aop的代理对象，检查一次
            Field[] declaredFields1 = v.getClass().getSuperclass().getDeclaredFields();
            for (Field field : declaredFields1) {
                valuezr(field, v);
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
                valuezr(declaredField, filterAdapter);
                zr(declaredField, filterAdapter);
                rpczr(declaredField, filterAdapter);
            }
            //aop的代理对象，检查一次
            Field[] declaredFields1 = filterAdapter.getClass().getSuperclass().getDeclaredFields();
            for (Field field : declaredFields1) {
                valuezr(field, filterAdapter);
                zr(field, filterAdapter);
                rpczr(field, filterAdapter);
            }
        });
    }

    /**
     * 对BeetlSql兼容
     */
    public static void BeetlSqlinit() {

        //检查下是否有Beetlsql的管理器
        Object sqlManager = IocUtil.getBean("org.beetl.sql.core.SQLManager");
        if (sqlManager == null) {
            return;
        }
        //Bean对象
        Map<String, Object> all = IocUtil.getAll();
        all.forEach((k, v) -> {
            //获取当前类的所有字段
            Field[] declaredFields = v.getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                beetlsqlzr(declaredField, v, sqlManager);
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
                beetlsqlzr(declaredField, filterAdapter, sqlManager);
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
                log.info("{}----->{}：装配完成，Rpc装配", proxy.getClass().getSimpleName(), v.getClass().getSimpleName());
            } catch (Exception e) {
                log.error("{}----->{}：装配错误:RPC代理生成失败", v.getClass().getSimpleName(), v.getClass().getSimpleName());
            }
        }
    }

    private static void valuezr(Field declaredField, Object v) {
        Value annotation = declaredField.getAnnotation(Value.class);
        if (annotation != null) {
            try {
                declaredField.setAccessible(true);
                PropUtil instance = PropUtil.getInstance();
                String s = instance.get(annotation.value());
                Object convert = ParameterUtil.convert(declaredField, s);
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
                        log.warn("装配警告，存在多个子类，建议通过Bean名字装配，避免装配错误");
                    }
                    bean = IocUtil.getBean(allClassByInterface.get(0));
                    findMsg = "按子类装配，" + declaredField.getType().getSimpleName();
                } else {
                    //Beetlsql，是动态获取，ioc不存在，所以就取消注入
                    BeetlSQL beetlSQL = declaredField.getType().getAnnotation(BeetlSQL.class);
                    if (beetlSQL != null) {
                        return;
                    }
                    log.error("装配错误:容器中未找到对应的Bean对象装备配,查找说明：{}", findMsg);
                    return;
                }
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
                log.error("装配错误:{}", e.getMessage());
            }
        }
    }


    /**
     * Beetlsql注入
     *
     * @param declaredField
     * @param v
     */
    private static void beetlsqlzr(Field declaredField, Object v, Object sqlManager) {
        //检查是否有注解@Autowired
        Autowired annotation = declaredField.getAnnotation(Autowired.class);
        if (annotation != null) {
            declaredField.setAccessible(true);
            //检查字段是类型是否被@Beetlsql标注
            BeetlSQL beetlSQL = declaredField.getType().getAnnotation(BeetlSQL.class);
            try {
                if (beetlSQL != null) {
                    Class<?> aClass = sqlManager.getClass();
                    Method[] methods = aClass.getMethods();
                    Method getMapper = null;
                    for (Method method : methods) {
                        if (method.getName().equals("getMapper")) {
                            getMapper = method;
                            break;
                        }
                    }
                    if (getMapper == null) {
                        return;
                    }
                    //这个就是Dao的接口的实现类，将他进行注入到其他地方
                    Object bean = getMapper.invoke(sqlManager, declaredField.getType());
                    //同类型注入
                    if (declaredField.getType().isAssignableFrom(bean.getClass())) {
                        declaredField.set(v, bean);
                        log.info("{}----->{}：装配完成，{}", new Object[]{bean.getClass().getSimpleName(), v.getClass().getSimpleName(), "BeetlSql注入"});
                    } else {
                        log.error("{}----->{}：装配错误:类型不匹配", v.getClass().getSimpleName(), v.getClass().getSimpleName());
                    }
                }
            } catch (Exception e) {
                log.error("装配错误:{}", e.getMessage());
            }
        }
    }


}
