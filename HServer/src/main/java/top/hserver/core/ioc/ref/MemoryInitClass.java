package top.hserver.core.ioc.ref;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hserver.core.ioc.annotation.Auto;
import top.hserver.core.ioc.annotation.Track;
import top.hserver.core.server.util.ClassLoadUtil;
import top.hserver.core.server.util.ExceptionUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hxm
 * 有些操作需要处理内存处理哈
 */
public class MemoryInitClass {
    private static final Logger log = LoggerFactory.getLogger(MemoryInitClass.class);
    public static final ConcurrentHashMap<String, Object> annMapMethod = new ConcurrentHashMap<>();
    private static final List<String> cache = new ArrayList<>();

    public static void init(String packageName) {
        if (packageName == null) {
            return;
        }
        try {
            List<Class<?>> classes = ClassLoadUtil.LoadClasses(packageName, true);
            ClassPool cp = ClassPool.getDefault();
            for (Class<?> aClass : classes) {
                CtClass cc = null;
                ClassClassPath classPath = new ClassClassPath(aClass);
                cp.insertClassPath(classPath);
                CtMethod[] methods;
                try {
                    methods = cp.getCtClass(aClass.getName()).getMethods();
                } catch (NoClassDefFoundError error) {
                    continue;
                }
                bake:
                for (CtMethod method : methods) {
                    Object[] annotations = method.getAnnotations();
                    for (Object annotation : annotations) {
                        Annotation annotation1 = (Annotation) annotation;
                        Annotation[] annotations1 = annotation1.annotationType().getAnnotations();
                        for (Annotation annotation2 : annotations1) {
                            if (annotation2.annotationType().getName().equals(Auto.class.getName())) {
                                if (cache.contains(aClass.getName())) {
                                    continue;
                                } else {
                                    cache.add(aClass.getName());
                                }
                                cc = cp.get(aClass.getName());
                                cc.freeze();
                                cc.defrost();
                                if (annotation1.annotationType().getName().equals(Track.class.getName())) {
                                    log.debug("被链路跟踪的类：{}", aClass.getName());
                                    initTrack(cc, cp, method);
                                }
                                continue bake;
                            }
                        }
                    }
                }
                if (cc != null) {
                    try {
                        cc.toClass();
                    } catch (Exception e) {
                        log.warn(e.getMessage());
                    }

                }
            }
        } catch (Exception e) {
            log.error(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 清除缓存
     */
    public static void closeCache() {
        cache.clear();
    }


    private static void initTrack(CtClass cc, ClassPool cp, CtMethod method) throws Exception {
        CtMethod[] methods = cc.getMethods();
        for (CtMethod declaredMethod : methods) {
            Object annotation = declaredMethod.getAnnotation(Track.class);
            if (annotation != null) {
                //提前放进去不然Linux下报错
                cp.insertClassPath(new ClassClassPath(CtMethod.class));
                String uuid = UUID.randomUUID().toString();
                annMapMethod.put(uuid, method);
                log.debug("被链路跟踪的方法：{}", declaredMethod.getName());
                declaredMethod.addLocalVariable("begin_hserver", CtClass.longType);
                declaredMethod.addLocalVariable("end_hserver", CtClass.longType);
                declaredMethod.addLocalVariable("trackAdapter_hserver", cp.get(List.class.getCanonicalName()));
                declaredMethod.addLocalVariable("clazz_hserver", cp.get(Class.class.getCanonicalName()));
                declaredMethod.addLocalVariable("annMethodObj", cp.get(CtMethod.class.getCanonicalName()));
                declaredMethod.insertBefore("begin_hserver=System.currentTimeMillis();");
                declaredMethod.insertBefore("annMethodObj = (javassist.CtMethod)top.hserver.core.ioc.ref.MemoryInitClass.annMapMethod.get(\"" + uuid + "\");");

                StringBuilder src = new StringBuilder();
                src.append("end_hserver=System.currentTimeMillis();");
                src.append("trackAdapter_hserver = top.hserver.core.ioc.IocUtil.getListBean(top.hserver.core.interfaces.TrackAdapter.class);");
                if (!Modifier.isStatic(declaredMethod.getModifiers())) {
                    //非静态
                    src.append("clazz_hserver = this.getClass();");
                } else {
                    //静态
                    src.append("clazz_hserver = " + cc.getName() + ".class;");
                }

                src.append("if (trackAdapter_hserver!=null)");
                src.append("{");
                src.append("for (int i = 0; i <trackAdapter_hserver.size() ; i++)");
                src.append("{");
                src.append(" ((top.hserver.core.interfaces.TrackAdapter)trackAdapter_hserver.get(i)).track(clazz_hserver,annMethodObj,Thread.currentThread().getStackTrace(), begin_hserver,end_hserver);");
                src.append("}");
                src.append("}");
                src.append("else");
                src.append("{");
                src.append("System.out.println(\"请实现，TrackAdapter接口，并用@Bean标注\");");
                src.append("}");
                declaredMethod.insertAfter(src.toString());
            }
        }

    }

}
