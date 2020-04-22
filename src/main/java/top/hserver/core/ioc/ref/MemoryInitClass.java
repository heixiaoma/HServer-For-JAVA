package top.hserver.core.ioc.ref;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import lombok.extern.slf4j.Slf4j;
import top.hserver.core.interfaces.TrackAdapter;
import top.hserver.core.ioc.annotation.Track;
import top.hserver.core.server.util.JavassistClassLoadUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * @author hxm
 * 有些操作需要处理内存处理哈
 */
@Slf4j
public class MemoryInitClass {

    public static void init(Class clazz) {
        if (clazz == null || clazz.getPackage() == null || clazz.getPackage().getName() == null) {
            return;
        }
        try {
            List<Class<?>> classes = JavassistClassLoadUtil.LoadClasses(clazz.getPackage().getName(), true);
            for (Class<?> aClass : classes) {
                Method[] methods = aClass.getMethods();
                bname:for (Method method : methods) {
                    Annotation[] annotations = method.getAnnotations();
                    for (Annotation annotation : annotations) {
                        if (annotation.annotationType().getName().equals(Track.class.getName())) {
                            log.debug("被链路跟踪的类：{}",aClass.getName());
                            ClassPool cp = ClassPool.getDefault();
                            CtClass cc = cp.get(aClass.getName());
                            cc.freeze();
                            cc.defrost();
                            //跟踪处理
                            initTrack(cc, cp);
                            //带扩展的的功能。。。。
                            cc.toClass();
                            break bname;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void initTrack(CtClass cc,ClassPool cp) throws Exception {
        CtMethod[] methods = cc.getMethods();
        for (CtMethod declaredMethod : methods) {
            Object annotation = declaredMethod.getAnnotation(Track.class);
            if (annotation!=null){
                log.debug("被链路跟踪的方法：{}",declaredMethod.getName());
                declaredMethod.addLocalVariable("begin_hserver", CtClass.longType);
                declaredMethod.addLocalVariable("end_hserver", CtClass.longType);
                declaredMethod.addLocalVariable("trackAdapter_hserver", cp.get(TrackAdapter.class.getCanonicalName()));
                declaredMethod.addLocalVariable("clazz_hserver", cp.get(Class.class.getCanonicalName()));
                declaredMethod.addLocalVariable("method_hserver", cp.get(Method.class.getCanonicalName()));
                declaredMethod.insertBefore("begin_hserver=System.currentTimeMillis();");

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
                src.append(" trackAdapter_hserver.track(clazz_hserver,Thread.currentThread().getStackTrace(), begin_hserver,end_hserver);");
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
