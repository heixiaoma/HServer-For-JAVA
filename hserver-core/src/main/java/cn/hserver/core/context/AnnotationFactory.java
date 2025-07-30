package cn.hserver.core.context;

import cn.hserver.core.context.handler.*;
import cn.hserver.core.ioc.bean.BeanDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnnotationFactory {

    private final static List<AnnotationHandler> annotationHandlers = new ArrayList<AnnotationHandler>(){
        {
            add(new ComponentHandler());
            add(new ConfigurationHandler());
            add(new HookHandler());
            add(new QueueListenerHandler());
        }
    };

    public static void processClass(Class<?> clazz, Map<String, BeanDefinition> beanDefinitions) {
        annotationHandlers.forEach(handler -> {
            handler.handle(clazz, beanDefinitions);
        });
    }
}
