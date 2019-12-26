package top.hserver.core.ioc.ref;


import top.hserver.core.ioc.annotation.*;
import top.hserver.core.ioc.util.ClassLoadUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

@Slf4j
public class ClasspathPackageScanner implements PackageScanner {

    private String basePackage;

    /**
     * 初始化
     *
     * @param basePackage
     */
    public ClasspathPackageScanner(String basePackage) {
        this.basePackage = basePackage;
    }

    @Override
    public List<Class<?>> getBeansPackage() throws IOException {
        List<Class<?>> clazzLis = new ArrayList<>();
        List<Class<?>> classes = ClassLoadUtil.LoadClasses(basePackage, true);
        for (Class<?> aClass : classes) {
            if (aClass.getAnnotation(Bean.class)!=null){
                clazzLis.add(aClass);
            }
        }
        return clazzLis;
    }

    @Override
    public List<Class<?>> getWebSocketPackage() throws IOException {
        List<Class<?>> clazzLis = new ArrayList<>();
        List<Class<?>> classes = ClassLoadUtil.LoadClasses(basePackage, true);
        for (Class<?> aClass : classes) {
            if (aClass.getAnnotation(WebSocket.class)!=null){
                clazzLis.add(aClass);
            }
        }
        return clazzLis;
    }


    @Override
    public List<Class<?>> getConfigurationPackage() throws IOException {
        List<Class<?>> clazzLis = new ArrayList<>();
        List<Class<?>> classes = ClassLoadUtil.LoadClasses(basePackage, true);
        for (Class<?> aClass : classes) {
            if (aClass.getAnnotation(Configuration.class)!=null){
                clazzLis.add(aClass);
            }
        }
        return clazzLis;
    }

    @Override
    public List<Class<?>> getControllersPackage() throws IOException {
        List<Class<?>> clazzLis = new ArrayList<>();
        List<Class<?>> classes = ClassLoadUtil.LoadClasses(basePackage, true);
        for (Class<?> aClass : classes) {
            if (aClass.getAnnotation(Controller.class)!=null){
                clazzLis.add(aClass);
            }
        }
        return clazzLis;
    }

    @Override
    public List<Class<?>> getHooksPackage() throws IOException {
        List<Class<?>> clazzLis = new ArrayList<>();
        List<Class<?>> classes = ClassLoadUtil.LoadClasses(basePackage, true);
        for (Class<?> aClass : classes) {
            if (aClass.getAnnotation(Hook.class)!=null){
                clazzLis.add(aClass);
            }
        }
        return clazzLis;
    }


    @Override
    public List<Class<?>> getFiltersPackage() throws IOException {
        List<Class<?>> clazzLis = new ArrayList<>();
        List<Class<?>> classes = ClassLoadUtil.LoadClasses(basePackage, true);
        for (Class<?> aClass : classes) {
            if (aClass.getAnnotation(Filter.class)!=null){
                clazzLis.add(aClass);
            }
        }
        return clazzLis;
    }


}