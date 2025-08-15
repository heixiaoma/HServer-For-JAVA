package cn.hserver.core.ioc.handler;

import cn.hserver.core.util.ObjConvertUtil;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public interface PopulateBeanHandler {

    List<PopulateBeanHandler> HANDLERS = new ArrayList<PopulateBeanHandler>(){
        {
            add(new ConfigurationPropertiesHandler());
            add(new AutowiredHandler());
            add(new ValueHandler());
        }
    };

    static void addHandler(PopulateBeanHandler handler) {
        HANDLERS.add(handler);
    }

    void populate(Object bean);

    default boolean populateValue(Object beanInstance,Field field,Object fieldValue) {
        try {
            try {
                // 使用Introspector获取BeanInfo
                BeanInfo beanInfo = Introspector.getBeanInfo(beanInstance.getClass());
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

                // 查找与字段匹配的属性描述符
                for (PropertyDescriptor pd : propertyDescriptors) {
                    if (pd.getName().equals(field.getName()) && pd.getWriteMethod() != null) {
                        Method setterMethod = pd.getWriteMethod();
                        // 确保setter方法是public的
                        if (Modifier.isPublic(setterMethod.getModifiers())) {
                            // 调用setter方法
                            if (field.getType().isAssignableFrom(fieldValue.getClass())) {
                                setterMethod.invoke(beanInstance, fieldValue);
                                return true; // 成功通过setter注入，跳过字段反射
                            }else {
                                setterMethod.invoke(beanInstance, ObjConvertUtil.convert(field.getType(),fieldValue.toString()));
                                return true;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // 忽略异常，继续尝试字段反射注入
            }
            // 2. 使用字段反射注入
            field.setAccessible(true);
            field.set(beanInstance, fieldValue);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
