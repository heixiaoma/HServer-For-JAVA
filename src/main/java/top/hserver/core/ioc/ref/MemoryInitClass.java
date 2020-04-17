package top.hserver.core.ioc.ref;

import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import top.hserver.core.interfaces.TrackAdapter;
import top.hserver.core.ioc.annotation.Track;
import top.hserver.core.server.util.JavassistClassLoadUtil;
import top.hserver.core.server.util.JsonResult;
import top.test.action.Hello;

import java.io.ByteArrayInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * @author hxm
 * 有些操作需要处理内存处理哈
 */
public class MemoryInitClass {

    public static void init(Class clazz) {
        try {
            List<Class<?>> classes = JavassistClassLoadUtil.LoadClasses(clazz.getPackage().getName(), true);
            for (Class<?> aClass : classes) {
                Method[] methods = aClass.getMethods();
                for (Method method : methods) {
                    Annotation[] annotations = method.getAnnotations();
                    for (Annotation annotation : annotations) {
                        if (annotation.annotationType().getName().equals(Track.class.getName())) {
                            initTrack(aClass, method);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void initTrack(Class aClass, Method method) throws Exception {
        try {
            ClassPool cp = ClassPool.getDefault();
            CtClass cc = cp.get(aClass.getName());
            cc.freeze();
            cc.defrost();
            name:
            for (CtMethod declaredMethod : cc.getDeclaredMethods()) {
                //方法名字相同
                if (declaredMethod.getName().equals(method.getName())) {
                    //参数长度相同
                    if (declaredMethod.getParameterTypes().length == method.getParameterTypes().length) {
                        //每一位类型也一样
                            int size = declaredMethod.getParameterTypes().length;
                                for (int i = 0; i < size; i++) {
                                    if (!declaredMethod.getParameterTypes()[i].getSimpleName().equals(method.getParameterTypes()[i])) {
                                        break name;
                                    }
                                }
                                declaredMethod.addLocalVariable("begin_hserver", CtClass.longType);
                                declaredMethod.addLocalVariable("end_hserver", CtClass.longType);
                                declaredMethod.addLocalVariable("trackAdapter_hserver",  cp.get(TrackAdapter.class.getCanonicalName()));
                                declaredMethod.addLocalVariable("clazz_hserver",  cp.get(Class.class.getCanonicalName()));
                                declaredMethod.addLocalVariable("stackTraceElement_hserver",  cp.get(StackTraceElement.class.getCanonicalName()));
//                                declaredMethod.addLocalVariable("method_hserver",  cp.get(StackTraceElement.class.getCanonicalName()));
                                declaredMethod.insertBefore("begin_hserver=System.currentTimeMillis();");
                                declaredMethod.insertAfter("end_hserver=System.currentTimeMillis();");
                                declaredMethod.insertAfter("trackAdapter_hserver = top.hserver.core.ioc.IocUtil.getBean(top.hserver.core.interfaces.TrackAdapter.class);");
                                if (!Modifier.isStatic(method.getModifiers())) {
                                    //非静态
                                    declaredMethod.insertAfter("clazz_hserver = this.getClass();");
                                }else {
                                    //静态
                                    declaredMethod.insertAfter("clazz_hserver = "+aClass.getName()+".class;");
                                }

                                declaredMethod.insertAfter("stackTraceElement_hserver =Thread.currentThread().getStackTrace()[1];");
//                                declaredMethod.insertAfter("method_hserver = new Object(){}.getClass().getEnclosingMethod();");
                                declaredMethod.insertAfter("if (trackAdapter_hserver!=null){\n" +
                                        "                                    trackAdapter_hserver.track(clazz_hserver,stackTraceElement_hserver, begin_hserver,end_hserver);\n" +
                                        "                                }else{System.out.println(\"请实现，TrackAdapter接口，并用@Bean标注\");\n}");
                                cc.toClass();
                    }
                }
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

}
