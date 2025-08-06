package cn.hserver.plugin.beetlsql.handler;

import cn.hserver.core.context.handler.AnnotationHandler;
import cn.hserver.core.ioc.bean.BeanDefinition;
import cn.hserver.plugin.beetlsql.annotation.BeetlSQL;

import java.util.List;
import java.util.Map;

public class BeetlSQLHandler implements AnnotationHandler {
    private final List<Class<?>> beetlsql;
    public BeetlSQLHandler(List<Class<?>> beetlsql) {
        this.beetlsql=beetlsql;
    }

    @Override
    public void handle(Class<?> clazz, Map<String, BeanDefinition> beanDefinitions) {
        if (clazz.isAnnotationPresent(BeetlSQL.class)) {
            beetlsql.add(clazz);
        }
    }
}
