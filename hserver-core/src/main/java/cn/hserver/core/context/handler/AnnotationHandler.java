package cn.hserver.core.context.handler;

import cn.hserver.core.ioc.bean.BeanDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface AnnotationHandler {
    List<AnnotationHandler> ANNOTATION_HANDLERS = new ArrayList<AnnotationHandler>(){
        {
            add(new ComponentHandler());
            add(new ConfigurationHandler());
            add(new HookHandler());
            add(new QueueListenerHandler());
            add(new TestRunWithHandler());
        }
    };

    static void addHandler(final AnnotationHandler annotationHandler) {
        ANNOTATION_HANDLERS.add(annotationHandler);
    }

    void handle(Class<?> clazz, Map<String, BeanDefinition> beanDefinitions);

    default void defaultHandler(Class<?> clazz, Map<String, BeanDefinition> beanDefinitions){
        BeanDefinition configBeanDef = new BeanDefinition();
        configBeanDef.setBeanClass(clazz);
        beanDefinitions.put(configBeanDef.getDefaultBeanName(), configBeanDef);
    }
}
