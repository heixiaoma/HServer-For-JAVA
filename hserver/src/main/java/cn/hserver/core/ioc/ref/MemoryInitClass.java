package cn.hserver.core.ioc.ref;

import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.hserver.HServerApplication;
import cn.hserver.core.interfaces.TrackAdapter;
import cn.hserver.core.server.context.ConstConfig;
import cn.hserver.core.server.util.ClassLoadUtil;
import cn.hserver.core.server.util.ExceptionUtil;

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
        //自己不跟踪
        if (packageName == null || packageName.startsWith("cn.hserver")) {
            return;
        }
        try {
            List<Class<?>> classes = ClassLoadUtil.LoadClasses(packageName, true);
            ClassPool cp = ClassPool.getDefault();
            br:
            for (Class<?> aClass : classes) {
                /**
                 * 主动过滤的不跟踪
                 */
                for (String trackNoPackage : ConstConfig.TRACK_NO_PACKAGES) {
                    if (aClass.getName().startsWith(trackNoPackage)) {
                        break br;
                    }
                }
                //主函数不能被跟踪，他已经被加载了
                if (aClass.getName().equals(HServerApplication.mainClass.getName())) {
                    continue;
                }
                CtClass cc = null;
                CtMethod[] methods;
                try {
                    ClassClassPath classPath = new ClassClassPath(aClass);
                    cp.insertClassPath(classPath);
                    CtClass ctClass = cp.getCtClass(aClass.getName());
                    //如果是接口就不跟踪了
                    if (ctClass.isInterface()) {
                        continue;
                    }
                    //如果是TrackAdapter子类的都不能被跟踪，不然就递归了
                    CtClass[] interfaces = ctClass.getInterfaces();
                    if (interfaces.length > 0) {
                        for (CtClass anInterface : interfaces) {
                            if (anInterface.getName().equals(TrackAdapter.class.getName())) {
                                continue br;
                            }
                        }
                    }
                    methods = ctClass.getDeclaredMethods();
                } catch (Throwable error) {
                    continue;
                }
                //有的方法没用放方法体只有成员变量，这种不能跟踪
                if (methods == null || methods.length == 0) {
                    continue;
                }
                for (CtMethod method : methods) {
                    //重复的不跟踪
                    if (cache.contains(aClass.getName())) {
                        continue;
                    } else {
                        cache.add(aClass.getName());
                    }
                    //抽象方法不跟踪
                    if (method.isEmpty()) {
                        continue;
                    }
                    cc = cp.get(aClass.getName());
                    cc.freeze();
                    cc.defrost();
                    log.debug("被链路跟踪的类：{}", aClass.getName());
                    initTrack(cc, cp, method);
                }
                if (cc != null) {
                    try {
                        cc.toClass();
                        cc.detach();
                    } catch (Exception e) {
                        log.warn(e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }

    /**
     * 清除缓存
     */
    public static void closeCache() {
        cache.clear();
    }


    private static void initTrack(CtClass cc, ClassPool cp, CtMethod method) {
        CtMethod[] methods = cc.getDeclaredMethods();
        for (CtMethod declaredMethod : methods) {
            String uuid = UUID.randomUUID().toString();
            try {
                //提前放进去不然Linux下报错
                cp.insertClassPath(new ClassClassPath(CtMethod.class));
                annMapMethod.put(uuid, declaredMethod);
                log.debug("被链路跟踪的方法：{}", declaredMethod.getName());
                //之前
                declaredMethod.addLocalVariable("begin_hserver", CtClass.longType);
                declaredMethod.addLocalVariable("end_hserver", CtClass.longType);
                declaredMethod.addLocalVariable("spanId", CtClass.longType);
                declaredMethod.addLocalVariable("pspanId", CtClass.longType);
                declaredMethod.addLocalVariable("trackAdapter_hserver", cp.get(List.class.getCanonicalName()));
                declaredMethod.addLocalVariable("clazz_hserver", cp.get(Class.class.getCanonicalName()));
                declaredMethod.addLocalVariable("annMethodObj", cp.get(CtMethod.class.getCanonicalName()));

                 String before = "begin_hserver=System.currentTimeMillis();" +
                        "spanId=cn.hserver.core.server.util.SpanUtil.get();" +
                        "pspanId=cn.hserver.core.server.util.SpanUtil.add();" +
                        "annMethodObj = (javassist.CtMethod)cn.hserver.core.ioc.ref.MemoryInitClass.annMapMethod.get(\"" + uuid + "\");";
                declaredMethod.insertBefore(before);


                //之后
                StringBuilder src = new StringBuilder();
                src.append("end_hserver=System.currentTimeMillis();");
                src.append("trackAdapter_hserver = cn.hserver.core.ioc.IocUtil.getListBean(cn.hserver.core.interfaces.TrackAdapter.class);");
                src.append("if (trackAdapter_hserver!=null)");
                src.append("{");
                if (!Modifier.isStatic(declaredMethod.getModifiers())) {
                    //非静态
                    src.append("clazz_hserver = this.getClass();");
                } else {
                    //静态
                    src.append("clazz_hserver = " + cc.getName() + ".class;");
                }
                src.append("for (int i = 0; i <trackAdapter_hserver.size() ; i++)");
                src.append("{");
                src.append(" ((cn.hserver.core.interfaces.TrackAdapter)trackAdapter_hserver.get(i)).track(clazz_hserver,annMethodObj,Thread.currentThread().getStackTrace(), begin_hserver,end_hserver,pspanId,spanId);");
                src.append("}");
                src.append("}");
                src.append("cn.hserver.core.server.util.SpanUtil.clear();");
                declaredMethod.insertAfter(src.toString());
            } catch (Exception e) {
                log.warn(method.getName() + "：" + e.getMessage());
                annMapMethod.remove(uuid);
            }
        }
    }

}
