package cn.hserver.plugin.beetlsql;

import cn.hserver.core.plugin.bean.PluginInfo;
import cn.hserver.core.plugin.handler.PluginAdapter;
import cn.hserver.plugin.beetlsql.annotation.BeetlSQL;
import org.beetl.sql.core.SQLManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * @author hxm
 */
public class BeetLSqlPlugin extends PluginAdapter {

    private static final Logger log = LoggerFactory.getLogger(BeetLSqlPlugin.class);



    @Override
    public void ioc(){


    }


    @Override
    public void iocInit(PackageScanner packageScanner) {
        //都装配完了，我去装配哈。BeetlSql
        final Map<String, SQLManager> sqlManagers = new HashMap<>(1);
        Map<String, Object> all1 = IocUtil.getAll();
        all1.forEach((k, v) -> {
            //存在sqlManager.那就搞事情；
            if (SQLManager.class.equals(v.getClass())) {
                sqlManagers.put(k, (SQLManager) v);
            }
        });
        try {
            Set<Class<?>> annotationList = packageScanner.getAnnotationList(BeetlSQL.class);
            for (Class<?> aClass : annotationList) {
                BeetlSQL beetlSQL = aClass.getAnnotation(BeetlSQL.class);
                SQLManager sqlManager;
                if (beetlSQL.value().trim().length() == 0) {
                    sqlManager = sqlManagers.get(SQLManager.class.getName());
                } else {
                    sqlManager = sqlManagers.get(beetlSQL.value());
                }
                Object mapper = sqlManager.getMapper(aClass);
                IocUtil.addBean(mapper);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public PluginInfo getPluginInfo() {
        return new PluginInfo.Builder()
                .name("beetlsql插件")
                .description("简洁方便，功能强大的ORM工具，从2015年开始研发")
                .build();
    }
}
