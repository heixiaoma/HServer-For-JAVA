package cn.hserver.core.ioc.handler;

import cn.hserver.core.ioc.annotation.Autowired;
import cn.hserver.core.ioc.annotation.Qualifier;

import java.lang.reflect.Field;

import static cn.hserver.core.context.AnnotationConfigApplicationContext.getBean;

public class AutowiredHandler implements PopulateBeanHandler{

    @Override
    public void populate(Object bean) {
        for (Field field : bean.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Object fieldValue;
                String beanName = null;

                // 检查字段上是否有@Qualifier注解
                Qualifier qualifier = field.getAnnotation(Qualifier.class);
                if (qualifier != null) {
                    beanName = qualifier.value();
                }

                if (beanName != null) {
                    fieldValue = getBean(beanName);
                } else {
                    fieldValue = getBean(field.getType());
                }
                if (fieldValue != null) {
                    populateValue(bean,field,fieldValue);
                }
            }
        }
    }
}
