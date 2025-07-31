package cn.hserver.core.ioc.handler;

import cn.hserver.core.config.ConfigData;
import cn.hserver.core.config.annotation.ConfigurationProperties;

import java.lang.reflect.Field;

public class ConfigurationPropertiesHandler implements PopulateBeanHandler{

    @Override
    public void populate(Object bean) {
        ConfigurationProperties configurationProperties = bean.getClass().getAnnotation(ConfigurationProperties.class);
        if (configurationProperties != null) {
            for (Field field : bean.getClass().getDeclaredFields()) {
                String configName = field.getName();
                if (!configurationProperties.prefix().isEmpty()) {
                    configName = configurationProperties.prefix() + "." + field.getName();
                }
                Object fieldValue = ConfigData.getInstance().get(configName,null);
                if (fieldValue != null) {
                    populateValue(bean, field, fieldValue);
                }
            }
        }
    }
}
