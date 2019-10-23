package com.test;

import com.hserver.HServerApplication;
import com.hserver.core.server.context.WebContext;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class WebApp {
    public static void main(String[] args) {
        Method[] methods = WebApp.class.getMethods();
        Type[] genericParameterTypes = methods[1].getGenericParameterTypes();
        for (Type genericParameterType : genericParameterTypes) {
            System.out.println(genericParameterType.getTypeName());
        }

        HServerApplication.run(WebApp.class);
    }

    public void aa(String ss, int a, WebContext webContext) {

    }
}
