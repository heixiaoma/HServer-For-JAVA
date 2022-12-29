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
    public boolean iocInitBean(Class classz) {
        return false;
    }

    @Override
    public void iocInit(PackageScanner packageScanner) {

    }

    @Override
    public void iocInitEnd() {

    }

    @Override
    public void startInjection() {

    }

    @Override
    public void injectionEnd() {
        //都装配完了，我去装配哈。BeetlSql
        final Map<String, SQLManager> sqlManagers = new HashMap<>(1);
        Map<String, Object> all1 = IocUtil.getAll();
        all1.forEach((k, v) -> {
            //存在sqlManager.那就搞事情；
            if (SQLManager.class.equals(v.getClass())) {
                sqlManagers.put(k, (SQLManager) v);
            }
        });

        //Bean对象
        Map<String, Object> all = IocUtil.getAll();
        all.forEach((k, v) -> {
            if (v instanceof List) {
                List v1 = (List) v;
                for (Object o : v1) {
                    //获取当前类的所有字段向上最加一层，有可能是代理类查不到
                    List<Field> objectField = getObjectField(o);
                    for (Field declaredField : objectField) {
                        beetlSqlInjection(declaredField, o, sqlManagers);
                    }
                }
            } else {
                //获取当前类的所有字段向上最加一层，有可能是代理类查不到
                List<Field> objectField = getObjectField(v);
                for (Field declaredField : objectField) {
                    beetlSqlInjection(declaredField, v, sqlManagers);
                }
            }
        });
        log.info("beetlsql插件执行完成");
    }


    /**
     * Beetlsql注入
     *
     * @param declaredField
     * @param v
     */
    private static void beetlSqlInjection(Field declaredField, Object v, Map<String, SQLManager> sqlManagers) {
        //检查是否有注解@Autowired
        Autowired annotation = declaredField.getAnnotation(Autowired.class);
        if (annotation != null) {
            declaredField.setAccessible(true);
            //检查字段是类型是否被@Beetlsql标注
            BeetlSQL beetlSQL = declaredField.getType().getAnnotation(BeetlSQL.class);
            try {
                if (beetlSQL != null) {
                    SQLManager sqlManager;
                    if (beetlSQL.value().trim().length() == 0) {
                        sqlManager = sqlManagers.get(SQLManager.class.getName());
                    } else {
                        sqlManager = sqlManagers.get(beetlSQL.value());
                    }

                    if (sqlManager == null) {
                        throw new NullPointerException("空指针，sqlManager-bean存在，但是BeetlSQL的注解的Value值（" + beetlSQL.value() + "） 类型不匹配,请检查配置类的Bean 名字和BeetlSQL 的是否一致.");
                    }
                    Object mapper = sqlManager.getMapper(declaredField.getType());
                    //同类型注入
                    if (declaredField.getType().isAssignableFrom(mapper.getClass())) {
                        declaredField.set(v, mapper);
                        log.info("{}----->{}：装配完成，{}", new Object[]{mapper.getClass().getSimpleName(), v.getClass().getSimpleName(), "BeetlSql注入"});
                    } else {
                        log.error("{}----->{}：装配错误:类型不匹配", v.getClass().getSimpleName(), v.getClass().getSimpleName());
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                throw new RuntimeException(e);
            }
        }
    }


    private static List<Field> getObjectField(Object clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> aClass = clazz.getClass();
        while (!aClass.equals(Object.class)) {
            Field[] declaredFields = aClass.getDeclaredFields();
            fields.addAll(Arrays.asList(declaredFields));
            aClass = aClass.getSuperclass();
        }
        return fields;
    }


}
