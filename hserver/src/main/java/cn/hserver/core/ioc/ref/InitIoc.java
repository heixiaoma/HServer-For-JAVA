package cn.hserver.core.ioc.ref;

import cn.hserver.core.ioc.ref.init.*;
import cn.hserver.core.plugs.PlugsManager;
import cn.hserver.core.server.util.*;
import javassist.util.proxy.ProxyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.hserver.core.queue.QueueDispatcher;
import cn.hserver.core.interfaces.*;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.annotation.*;
import cn.hserver.core.task.TaskManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author hxm
 */
public class InitIoc {
    private static final Logger log = LoggerFactory.getLogger(InitBean.class);

    private static void sortOrder() {
        IocUtil.getAll().forEach((k, v) -> {
            if (v instanceof List) {
                List<?> listBean = (List<?>) v;
                CopyOnWriteArrayList<Object> newObjectList = new CopyOnWriteArrayList<>();
                if (listBean.size() > 1) {
                    int temp = 1;
                    for (Object o : listBean) {
                        Order annotation = o.getClass().getAnnotation(Order.class);
                        if (annotation != null) {
                            if (temp > annotation.value()) {
                                //向后添加
                                newObjectList.add(0, o);
                                temp = annotation.value();
                            } else {
                                //向前添加
                                newObjectList.add(o);
                                temp = annotation.value();
                            }
                        } else {
                            newObjectList.add(o);
                        }
                    }
                    IocUtil.remove(k);
                    IocUtil.addBean(k, newObjectList);
                }
            }
        });
    }


    /**
     * 加载所有bean进容器
     */
    public static void init(Set<String> packageNames) {
        if (packageNames == null) {
            return;
        }
        try(PackageScanner scan = new ClasspathPackageScanner(packageNames)) {
            //读取配置文件
            new InitConfigurationProperties(packageNames).init(scan);
            //测试类
            new InitTest(packageNames).init(scan);

            //初始化容器bean
            new InitBean(packageNames).init(scan);

            //初始化配置类
            new InitConfiguration(packageNames).init(scan);

            //初始化Hook
            new InitHook(packageNames).init(scan);

            //插件初始
            new InitPlugin(packageNames).init(scan);

            //初始化队列
            new InitQueue(packageNames).init(scan);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        //排序
        sortOrder();
    }


    /**
     * 给所有bean分配依赖(自动装配)
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


    public static void autoZr(Object v) {
        Class par = v.getClass();
        while (!par.equals(Object.class)) {
            //获取当前类的所有字段
            Field[] declaredFields = par.getDeclaredFields();
            for (Field field : declaredFields) {
                //Value 注入
                valuezr(field, v);
                //Autowired注入
                zr(field, v);
            }
            par = par.getSuperclass();
        }
    }


    /**
     * 自动配置类里面，普通Bean，配置Props的
     *
     * @param declaredField
     * @param v
     */
    public static void valuezr(Field declaredField, Object v) {
        Value annotation = declaredField.getAnnotation(Value.class);
        if (annotation != null) {
            try {
                declaredField.setAccessible(true);
                PropUtil instance = PropUtil.getInstance();
                String s = instance.get(annotation.value());
                Object convert = ObjConvertUtil.convert(declaredField.getType(), s);
                declaredField.set(v, convert);
            } catch (Exception e) {
                log.error("{}----->{}：@Value装配错误", v.getClass().getSimpleName(), v.getClass().getSimpleName());
                throw new RuntimeException(e);
            }
        }
    }


    public static void zr(Field declaredField, Object v) {
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
                            Object bean1 = IocUtil.getBean(aClass);
                            if (bean1 == null) {
                                continue;
                            }
                            if (tempCode == 0) {
                                tempCode = bean1.hashCode();
                            } else {
                                if (tempCode != bean1.hashCode()) {
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
                    log.debug("{}----->{}：装配完成，{}", bean.getClass().getSimpleName(), v.getClass().getSimpleName(), findMsg);
                    //父类检测注入
                } else if (declaredField.getType().isAssignableFrom(bean.getClass())) {
                    declaredField.set(v, bean);
                    log.debug("{}----->{}：装配完成，{}", bean.getClass().getSimpleName(), v.getClass().getSimpleName(), findMsg);
                } else {
                    log.error("{}----->{}：装配错误:类型不匹配", v.getClass().getSimpleName(), v.getClass().getSimpleName());
                }
            } catch (Exception e) {
                log.error("装配错误:{},{}", declaredField.getName(), e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }


}
