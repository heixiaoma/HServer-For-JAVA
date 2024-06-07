package cn.hserver.plugin.beetlsql;

import cn.hserver.core.ioc.ref.PackageScanner;
import cn.hserver.plugin.beetlsql.annotation.BeetlSQL;
import cn.hserver.core.interfaces.PluginAdapter;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.annotation.Autowired;
import org.beetl.sql.core.SQLManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;


/**
 * @author hxm
 */
public class BeetLSqlPlugin implements PluginAdapter {

    private static final Logger log = LoggerFactory.getLogger(BeetLSqlPlugin.class);

    @Override
    public void startApp() {

    }

    @Override
    public void startIocInit() {

    }

    @Override
    public Set<Class<?>> iocInitBeanList() {
        return null;
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
    public void iocInitEnd() {

    }

    @Override
    public void startInjection() {

    }

    @Override
    public void injectionEnd() {
        log.info("Beetlsql插件执行完成");
    }

}
