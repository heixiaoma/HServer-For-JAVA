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
        // 注册配置类本身作为Bean
        String className = clazz.getName();
        BeanDefinition configBeanDef = new BeanDefinition();
        configBeanDef.setBeanClass(clazz);
        String configBeanName = className.substring(className.lastIndexOf('.') + 1);
        configBeanName = Character.toLowerCase(configBeanName.charAt(0)) + configBeanName.substring(1);
        beanDefinitions.put(configBeanName, configBeanDef);
    }
}
