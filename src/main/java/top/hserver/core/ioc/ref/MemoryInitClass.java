package top.hserver.core.ioc.ref;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import lombok.extern.slf4j.Slf4j;
import top.hserver.core.interfaces.AnnotationAdapter;
import top.hserver.core.interfaces.TrackAdapter;
import top.hserver.core.ioc.annotation.Auto;
import top.hserver.core.ioc.annotation.Track;
import top.hserver.core.server.util.JavassistClassLoadUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hxm
 * 有些操作需要处理内存处理哈
 */
@Slf4j
public class MemoryInitClass {

    public static final ConcurrentHashMap<String,Object> annMapMethod=new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String,Object> annMapObject=new ConcurrentHashMap<>();


    public static void init(Class clazz) {
        if (clazz == null || clazz.getPackage() == null || clazz.getPackage().getName() == null) {
            return;
        }
        try {
            List<Class<?>> classes = JavassistClassLoadUtil.LoadClasses(clazz.getPackage().getName(), true);
            ClassPool cp = ClassPool.getDefault();
            for (Class<?> aClass : classes) {
                CtClass cc = null;
                Method[] methods = aClass.getMethods();
                bname:
                for (Method method : methods) {
                    Annotation[] annotations = method.getAnnotations();
                    for (Annotation annotation : annotations) {
                        for (Annotation annotation1 : annotation.annotationType().getAnnotations()) {
                            if (annotation1.annotationType().getName().equals(Auto.class.getName())) {
                                cc = cp.get(aClass.getName());
                                cc.freeze();
                                cc.defrost();

                                if (!annotation.annotationType().getName().equals(Track.class.getName())) {
                                    log.debug("自定义注解：{}", annotation.annotationType().getName());
                                    autoAnnotation(cc, cp, annotation.annotationType(),method);
                                }

                                if (annotation.annotationType().getName().equals(Track.class.getName())) {
                                    log.debug("被链路跟踪的类：{}", aClass.getName());
                                    initTrack(cc, cp,method);
                                }
                                continue bname;
                            }
                        }
                    }
                }
                if (cc != null) {
                    cc.toClass();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void initTrack(CtClass cc, ClassPool cp,Method method) throws Exception {
        CtMethod[] methods = cc.getMethods();
        for (CtMethod declaredMethod : methods) {
            Object annotation = declaredMethod.getAnnotation(Track.class);
            if (annotation != null) {
                String uuid = UUID.randomUUID().toString();
                annMapMethod.put(uuid,method);
                log.debug("被链路跟踪的方法：{}", declaredMethod.getName());
                declaredMethod.addLocalVariable("begin_hserver", CtClass.longType);
                declaredMethod.addLocalVariable("end_hserver", CtClass.longType);
                declaredMethod.addLocalVariable("trackAdapter_hserver", cp.get(TrackAdapter.class.getCanonicalName()));
                declaredMethod.addLocalVariable("clazz_hserver", cp.get(Class.class.getCanonicalName()));
                declaredMethod.addLocalVariable("annMethodObj", cp.get(Method.class.getCanonicalName()));
                declaredMethod.insertBefore("begin_hserver=System.currentTimeMillis();");
                declaredMethod.insertBefore("annMethodObj = (java.lang.reflect.Method)top.hserver.core.ioc.ref.MemoryInitClass.annMapMethod.get(\""+uuid+"\");");

                StringBuilder src = new StringBuilder();
                src.append("end_hserver=System.currentTimeMillis();");
                src.append("trackAdapter_hserver = top.hserver.core.ioc.IocUtil.getBean(top.hserver.core.interfaces.TrackAdapter.class);");
                if (!Modifier.isStatic(declaredMethod.getModifiers())) {
                    //非静态
                    src.append("clazz_hserver = this.getClass();");
                } else {
                    //静态
                    src.append("clazz_hserver = " + cc.getName() + ".class;");
                }
                src.append("if (trackAdapter_hserver!=null)");
                src.append("{");
                src.append(" trackAdapter_hserver.track(clazz_hserver,annMethodObj,Thread.currentThread().getStackTrace(), begin_hserver,end_hserver);");
                src.append("}");
                src.append("else");
                src.append("{");
                src.append("System.out.println(\"请实现，TrackAdapter接口，并用@Bean标注\");");
                src.append("}");
                declaredMethod.insertAfter(src.toString());
            }
        }

    }


    private static void autoAnnotation(CtClass cc, ClassPool cp, Class c,Method method) throws Exception {
        CtMethod[] methods = cc.getMethods();
        for (CtMethod declaredMethod : methods) {
            Object annotation = declaredMethod.getAnnotation(c);

            if (annotation != null) {
                String uuid = UUID.randomUUID().toString();
                annMapObject.put(uuid,annotation);
                annMapMethod.put(uuid,method);
                log.debug("被注解的方法：{}", declaredMethod.getName());
                declaredMethod.addLocalVariable("annAdapter_hserver", cp.get(AnnotationAdapter.class.getCanonicalName()));
                declaredMethod.addLocalVariable("annObj", cp.get(Annotation.class.getCanonicalName()));
                declaredMethod.addLocalVariable("clazz_hserver_x", cp.get(Class.class.getCanonicalName()));
                StringBuilder insertBefore = new StringBuilder();
                insertBefore.append("annAdapter_hserver = top.hserver.core.ioc.IocUtil.getBean(top.hserver.core.interfaces.AnnotationAdapter.class);");
                insertBefore.append("annObj = (java.lang.annotation.Annotation)top.hserver.core.ioc.ref.MemoryInitClass.annMapObject.get(\""+uuid+"\");");
                if (!Modifier.isStatic(declaredMethod.getModifiers())) {
                    //非静态
                    insertBefore.append("clazz_hserver_x = this.getClass();");
                } else {
                    //静态
                    insertBefore.append("clazz_hserver_x = " + cc.getName() + ".class;");
                }
                insertBefore.append("if (annAdapter_hserver!=null)");
                insertBefore.append("{");
                insertBefore.append(" annAdapter_hserver.before(annObj,$args,clazz_hserver_x);");
                insertBefore.append("}");
                insertBefore.append("else");
                insertBefore.append("{");
                insertBefore.append("System.out.println(\"请实现，AnnotationAdapter接口，并用@Bean标注\");");
                insertBefore.append("}");
                declaredMethod.insertBefore(insertBefore.toString());

                StringBuilder insertAfter = new StringBuilder();
                insertAfter.append("if (annAdapter_hserver!=null)");
                insertAfter.append("{");
                insertAfter.append("annAdapter_hserver.after(annObj,$_,clazz_hserver_x);");
                insertAfter.append("}");
                insertAfter.append("else");
                insertAfter.append("{");
                insertAfter.append("System.out.println(\"请实现，AnnotationAdapter接口，并用@Bean标注\");");
                insertAfter.append("}");
                declaredMethod.insertAfter(insertAfter.toString());
            }
        }

    }


}
