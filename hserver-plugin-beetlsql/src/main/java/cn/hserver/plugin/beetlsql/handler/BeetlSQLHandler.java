package cn.hserver.plugin.beetlsql.handler;

import cn.hserver.core.context.IocApplicationContext;
import cn.hserver.core.context.handler.AnnotationHandler;
import cn.hserver.core.ioc.bean.BeanDefinition;
import cn.hserver.plugin.beetlsql.annotation.BeetlSQL;
import org.beetl.sql.core.SQLManager;

import java.util.Map;

public class BeetlSQLHandler implements AnnotationHandler {
    @Override
    public void handle(Class<?> clazz, Map<String, BeanDefinition> beanDefinitions) {
        if (clazz.isAnnotationPresent(BeetlSQL.class)) {
            BeetlSQL beetlSQL = clazz.getAnnotation(BeetlSQL.class);
            SQLManager sqlManager;
            if (beetlSQL.value().trim().isEmpty()){
                sqlManager = IocApplicationContext.getBean(SQLManager.class);
            }else {
                sqlManager=(SQLManager) IocApplicationContext.getBean(beetlSQL.value());
            }
            Object mapper = sqlManager.getMapper(clazz);
            IocApplicationContext.addBean(mapper);
        }
    }
}
