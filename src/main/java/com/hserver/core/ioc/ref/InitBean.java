package com.hserver.core.ioc.ref;

import com.hserver.core.ioc.IocUtil;
import com.hserver.core.ioc.annotation.*;
import com.hserver.core.server.router.RouterInfo;
import com.hserver.core.server.router.RouterManager;
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

            /**
             * 检查是否有方法注解
             */
            List<String> actionsPackages = scan.getActionsPackage();
            for (String packge : actionsPackages) {
                Class aClass = Class.forName(packge);
                //检查注解里面是否有值
                Method[] methods = aClass.getDeclaredMethods();
                for (Method method : methods) {
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 给所有bean分配依赖
     */
    public static void injection() {
        Map<String, Object> all = IocUtil.getAll();
        all.forEach((k, v) -> {
            //获取当前类的所有字段
            Field[] declaredFields = v.getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                //检查是否有注解@In
                In annotation = declaredField.getAnnotation(In.class);
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
                        continue;
                    }
                    try {
                        if (bean.getClass().getName().equals(declaredField.getType().getName())) {
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

        });


    }

}
