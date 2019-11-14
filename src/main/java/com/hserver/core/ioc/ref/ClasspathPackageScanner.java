package com.hserver.core.ioc.ref;


import com.hserver.core.ioc.annotation.Bean;
import com.hserver.core.ioc.annotation.Controller;
import com.hserver.core.ioc.annotation.Hook;
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
        Set<Class<?>> beans = reflections.getTypesAnnotatedWith(Bean.class);
        for (Class<?> cl : beans) {
            clazzLis.add(cl.getCanonicalName());
        }
        return clazzLis;
    }

    @Override
    public List<String> getControllersPackage() throws IOException {
        List<String> clazzLis = new ArrayList<>();
        Reflections reflections = new Reflections(basePackage + ".*");
        Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);
        for (Class<?> cl : controllers) {
            clazzLis.add(cl.getCanonicalName());
        }
        return clazzLis;
    }

    @Override
    public List<String> getHooksPackage() throws IOException {
        List<String> clazzLis = new ArrayList<>();
        Reflections reflections = new Reflections(basePackage + ".*");
        Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Hook.class);
        for (Class<?> cl : controllers) {
            clazzLis.add(cl.getCanonicalName());
        }
        return clazzLis;
    }
}