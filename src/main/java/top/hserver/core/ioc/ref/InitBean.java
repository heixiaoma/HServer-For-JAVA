package top.hserver.core.ioc.ref;

import top.hserver.cloud.CloudManager;
import top.hserver.cloud.bean.ClientData;
import top.hserver.cloud.proxy.CloudProxy;
import top.hserver.core.interfaces.GlobalException;
import top.hserver.core.interfaces.InitRunner;
import top.hserver.core.ioc.IocUtil;
import top.hserver.core.interfaces.FilterAdapter;
import top.hserver.core.ioc.annotation.*;
import top.hserver.core.proxy.JavassistProxyFactory;
import top.hserver.core.server.filter.FilterChain;
import top.hserver.core.server.handlers.WebSocketServerHandler;
import top.hserver.core.server.router.RouterInfo;
import top.hserver.core.server.router.RouterManager;
import top.hserver.core.server.util.ParameterUtil;
import top.hserver.core.task.TaskManager;
import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                ClientData clientData=new ClientData();
                clientData.setAClass(aClass);
                clientData.setClassName(aClass.getName());
                clientData.setMethods(methods);
                if (rpcService.value().trim().length() > 0) {
                    //自定义了Rpc服务名
                    CloudManager.add(rpcService.value(), clientData);
                } else {
                    //没有自定义服务名字
                    CloudManager.add(aClass.getName(), clientData);
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
                    routerInfo.setaClass(aClass);
                    routerInfo.setReqMethodName(HttpMethod.GET);
                    RouterManager.addRouter(routerInfo);
                }
                if (post != null) {
                    RouterInfo routerInfo = new RouterInfo();
                    routerInfo.setMethod(method);
                    routerInfo.setUrl(post.value());
                    routerInfo.setaClass(aClass);
                    routerInfo.setReqMethodName(HttpMethod.POST);
                    RouterManager.addRouter(routerInfo);
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
     * 给所有bean分配依赖(自动装配)
     */
    public static void injection() {
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
    }


    /**
     * Rpc 服务的代理对象生成
     */
    private static void rpczr(Field declaredField, Object v) {
        Resource annotation = declaredField.getAnnotation(Resource.class);
        if (annotation != null) {
            try {
                declaredField.setAccessible(true);
                System.out.println(declaredField.getType());
                declaredField.set(v, CloudProxy.getProxy(declaredField.getType()));
            } catch (Exception e) {
                log.error(v.getClass().getName() + "----->" + declaredField.getName() + "：装配错误:RPC代理生成失败");
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
                findMsg = "按自定义名字装配，" + declaredField.getType();
            } else {
                findMsg = "按类型装配，" + declaredField.getType();
                bean = IocUtil.getBean(declaredField.getType());
            }
            if (bean == null) {
                log.error("装配错误:容器中未找到对应的Bean对象装备配,查找说明：" + findMsg);
                return;
            }
            try {
                if (bean.getClass().getName().contains(declaredField.getType().getName())) {
                    declaredField.set(v, bean);
                    log.info(v.getClass().getName() + "----->" + declaredField.getName() + "：装配完成");
                } else {
                    log.error(v.getClass().getName() + "----->" + declaredField.getName() + "：装配错误:类型不匹配");
                }
            } catch (Exception e) {
                log.error("装配错误:" + e.getMessage());
            }
        }
    }

}
