package com.hserver.core.ioc.ref;


import com.hserver.core.ioc.annotation.Bean;
import com.hserver.core.ioc.annotation.Controller;
import com.hserver.core.ioc.annotation.Filter;
import com.hserver.core.ioc.annotation.Hook;
import com.hserver.core.ioc.util.ClassLoadUtil;
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