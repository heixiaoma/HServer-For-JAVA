package cn.hserver.plugin.forest.handler;

import cn.hserver.core.context.IocApplicationContext;
import cn.hserver.core.context.handler.AnnotationHandler;
import cn.hserver.core.ioc.bean.BeanDefinition;
import com.dtflys.forest.Forest;
import com.dtflys.forest.annotation.ForestClient;

import java.util.Map;

public class ForestClientHandler implements AnnotationHandler {
    @Override
    public void handle(Class<?> clazz, Map<String, BeanDefinition> beanDefinitions) {
        if (clazz.isAnnotationPresent(ForestClient.class)) {
            Object data = Forest.client(clazz);
            if (data != null) {
                IocApplicationContext.addBean(data);
            }
        }
    }
}
