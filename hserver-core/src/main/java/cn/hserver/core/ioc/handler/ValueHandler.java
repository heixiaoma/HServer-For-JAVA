package cn.hserver.core.ioc.handler;

import cn.hserver.core.config.ConfigData;
import cn.hserver.core.config.annotation.Value;
import cn.hserver.core.ioc.annotation.Autowired;
import cn.hserver.core.ioc.annotation.Qualifier;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static cn.hserver.core.context.AnnotationConfigApplicationContext.getBean;

public class ValueHandler implements PopulateBeanHandler{

    @Override
    public void populate(Object bean) {
        for (Field field : bean.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Value.class)) {
                Value value = field.getAnnotation(Value.class);
                Object fieldValue = ConfigData.getInstance().get(value.value(),null);
                if (fieldValue != null) {
                    populateValue(bean, field, fieldValue);
                }
            }
        }
    }
}
