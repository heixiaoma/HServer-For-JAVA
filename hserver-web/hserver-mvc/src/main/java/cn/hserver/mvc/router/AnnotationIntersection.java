package cn.hserver.mvc.router;

import cn.hserver.mvc.annotation.router.*;
import cn.hserver.mvc.constants.HttpMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class AnnotationIntersection {
    // HTTP方法注解类数组
    private static final Class<?>[] HTTP_METHOD_ANNOTATIONS = {
            GET.class, POST.class, HEAD.class, PUT.class,
            PATCH.class, DELETE.class, OPTIONS.class, CONNECT.class, TRACE.class
    };

    /**
     * 查找方法注解与HTTP方法注解的交集
     */
    public static Map<String, List<HttpMethod>> findHttpMethodAnnotations(String basePath,Method method) {
        Map<String, List<HttpMethod>> result = new HashMap<>();
        Annotation[] methodAnnotations = method.getAnnotations();
        // 遍历方法上的所有注解
        for (Annotation annotation : methodAnnotations) {
            // 获取注解的类型（如@GET的类型是GET.class）
            Class<? extends Annotation> annotationType = annotation.annotationType();
            // 检查是否在HTTP方法注解数组中
            Class<?> inArray = isInArray(annotationType);
            if (inArray!=null) {
                try {
                    Method value = inArray.getMethod("value");
                    value.setAccessible(true);
                    String path = value.invoke(annotation).toString();
                    List<HttpMethod> httpMethods = result.computeIfAbsent(basePath + path, k -> new ArrayList<>());
                    httpMethods.add(HttpMethod.valueOf(annotation.annotationType().getSimpleName()));
                } catch (Exception ignored) {
                }
            }
            //检查通用
            if (annotation.annotationType().equals(RequestMapping.class)){
                RequestMapping requestMapping = (RequestMapping) annotation;
                String path = requestMapping.value();
                List<HttpMethod> httpMethods = result.computeIfAbsent(basePath + path, k -> new ArrayList<>());
                HttpMethod[] method1 = requestMapping.method();
                if (method1.length==0) {
                    for (Class<?> httpMethodAnnotation : HTTP_METHOD_ANNOTATIONS) {
                        httpMethods.add(HttpMethod.valueOf(httpMethodAnnotation.getSimpleName()));
                    }
                }else {
                    httpMethods.addAll(Arrays.asList(method1));
                }
            }
        }
        return result;
    }

    /**
     * 检查某个类是否在指定的类数组中
     */
    private static Class<?> isInArray(Class<?> target) {
        for (Class<?> clazz : AnnotationIntersection.HTTP_METHOD_ANNOTATIONS) {
            // 比较类对象是否相同
            if (clazz.equals(target)) {
                return clazz;
            }
        }
        return null;
    }

}
