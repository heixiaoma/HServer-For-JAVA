package com.hserver.core.ioc.ref;


import com.hserver.core.ioc.annotation.Action;
import com.hserver.core.ioc.annotation.Bean;
import org.reflections.Reflections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    public List<String> getBeansPackage() throws IOException {
        List<String> clazzLis = new ArrayList<>();
        Reflections reflections = new Reflections(basePackage + ".*");
        //比如可以获取有Pay注解的class
        Set<Class<?>> beans = reflections.getTypesAnnotatedWith(Bean.class);
        for (Class<?> cl : beans) {
            clazzLis.add(cl.getCanonicalName());
        }
        return clazzLis;
    }

    @Override
    public List<String> getActionsPackage() throws IOException {
        List<String> clazzLis = new ArrayList<>();
        Reflections reflections = new Reflections(basePackage + ".*");
        //比如可以获取有Pay注解的class
        Set<Class<?>> actions = reflections.getTypesAnnotatedWith(Action.class);
        for (Class<?> cl : actions) {
            clazzLis.add(cl.getCanonicalName());
        }
        return clazzLis;
    }

}