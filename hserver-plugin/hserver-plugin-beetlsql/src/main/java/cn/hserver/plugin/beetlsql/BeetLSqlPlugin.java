package cn.hserver.plugin.beetlsql;

import cn.hserver.core.context.IocApplicationContext;
import cn.hserver.core.context.handler.AnnotationHandler;
import cn.hserver.core.ioc.bean.BeanDefinition;
import cn.hserver.core.plugin.bean.PluginInfo;
import cn.hserver.core.plugin.handler.PluginAdapter;
import cn.hserver.plugin.beetlsql.annotation.BeetlSQL;
import cn.hserver.plugin.beetlsql.handler.BeetlSQLHandler;
import org.beetl.sql.core.SQLManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author hxm
 */
public class BeetLSqlPlugin extends PluginAdapter {

    private static final Logger log = LoggerFactory.getLogger(BeetLSqlPlugin.class);

    private static final List<Class<?>> beetlsql=new ArrayList<>();


    @Override
    public void iocStartScan(Class<?> clazz) {
        if (clazz.isAnnotationPresent(BeetlSQL.class)) {
            beetlsql.add(clazz);
        }
    }

    @Override
    public void iocStartPopulate(){
        for (Class<?> aClass : beetlsql) {
            BeetlSQL beetlSQL =aClass.getAnnotation(BeetlSQL.class);
            if (beetlSQL!=null) {
                SQLManager sqlManager;
                if (beetlSQL.value().trim().isEmpty()) {
                    sqlManager = IocApplicationContext.getBean(SQLManager.class);
                } else {
                    sqlManager = (SQLManager) IocApplicationContext.getBean(beetlSQL.value());
                }
                if (sqlManager != null) {
                    Object mapper = sqlManager.getMapper(aClass);
                    IocApplicationContext.addBean(mapper);
                }
            }
        }
        beetlsql.clear();
    }

    @Override
    public PluginInfo getPluginInfo() {
        return new PluginInfo.Builder()
                .name("beetlsql插件")
                .description("简洁方便，功能强大的ORM工具，从2015年开始研发")
                .build();
    }
}
