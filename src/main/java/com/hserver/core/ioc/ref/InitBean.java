package com.hserver.core.ioc.ref;

import com.hserver.core.ioc.IocUtil;
import com.hserver.core.ioc.annotation.*;
import com.hserver.core.proxy.JavassistProxyFactory;
import com.hserver.core.server.router.RouterInfo;
import com.hserver.core.server.router.RouterManager;
import com.hserver.core.server.util.ParameterUtil;
import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
            PackageScanner scan = new ClasspathPackageScanner(baseClass.getPackage().getName());
            initBean(scan);
            initController(scan);
            initHook(scan);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 初始化Bean
     */
    private static void initBean(PackageScanner scan) throws Exception {

        List<String> packges = scan.getBeansPackage();
        for (String packge : packges) {
            Class aClass = Class.forName(packge);
            //检查注解里面是否有值
            Bean annotation = (Bean) aClass.getAnnotation(Bean.class);
            if (annotation != null && annotation.value().trim().length() > 0) {
                IocUtil.addBean(annotation.value(), aClass.newInstance());
            } else {
                IocUtil.addBean(aClass.getName(), aClass.newInstance());
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
        List<String> actionsPackages = scan.getControllersPackage();
        for (String packge : actionsPackages) {
            Class aClass = Class.forName(packge);
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


    private static void initHook(PackageScanner scan) throws Exception {
        JavassistProxyFactory javassistProxyFactory = new JavassistProxyFactory();
        List<String> packges = scan.getHooksPackage();
        for (String packge : packges) {
            Class aClass = Class.forName(packge);
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
                zr(declaredField, k, v);
            }
            //aop的代理对象，检查一次
            Field[] declaredFields1 = v.getClass().getSuperclass().getDeclaredFields();
            for (Field field : declaredFields1) {
                zr(field, k, v);
            }

        });
    }

    private static void zr(Field declaredField, String k, Object v) {
        declaredField.setAccessible(true);
        //检查是否有注解@Autowired
        Autowired annotation = declaredField.getAnnotation(Autowired.class);
        if (annotation != null) {
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
                    log.info(v.getClass().getName() + "----->" + declaredField.getName() + "装配完成");
                } else {
                    log.error(v.getClass().getName() + "----->" + declaredField.getName() + "装配错误:类型不匹配");
                }
            } catch (Exception e) {
                log.error("装配错误:" + e.getMessage());
            }
        }
    }

}
