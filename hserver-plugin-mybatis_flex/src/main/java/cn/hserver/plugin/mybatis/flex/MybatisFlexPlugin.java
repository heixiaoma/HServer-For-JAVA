package cn.hserver.plugin.mybatis.flex;

import cn.hserver.core.interfaces.PluginAdapter;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.annotation.Autowired;
import cn.hserver.core.ioc.ref.PackageScanner;
import cn.hserver.plugin.mybatis.flex.annotation.Mybatis;
import com.mybatisflex.core.MybatisFlexBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

public class MybatisFlexPlugin implements PluginAdapter {
    private static final Logger log = LoggerFactory.getLogger(MybatisFlexPlugin.class);

    private MybatisFlexBootstrap mybatisFlexBootstrap = null;

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
        //开始把自己的Sql装备进去
        Set<Class<?>> classes = new HashSet<>();
        Map<String, Object> all = IocUtil.getAll();
        all.forEach((k, v) -> {
            if (v instanceof List) {
                List v1 = (List) v;
                for (Object o : v1) {
                    //获取当前类的所有字段向上最加一层，有可能是代理类查不到
                    List<Field> objectField = getObjectField(o);
                    for (Field declaredField : objectField) {
                        mybatisScan(declaredField, classes);
                    }
                }
            } else {
                //获取当前类的所有字段向上最加一层，有可能是代理类查不到
                List<Field> objectField = getObjectField(v);
                for (Field declaredField : objectField) {
                    mybatisScan(declaredField, classes);
                }
            }
        });
        mybatisFlexBootstrap = MybatisFlexConfig.init(classes);
    }

    @Override
    public void startInjection() {

    }

    @Override
    public void injectionEnd() {
        if (mybatisFlexBootstrap == null) {
            return;
        }
        //Bean对象
        Map<String, Object> all = IocUtil.getAll();
        all.forEach((k, v) -> {
            if (v instanceof List) {
                List v1 = (List) v;
                for (Object o : v1) {
                    //获取当前类的所有字段向上最加一层，有可能是代理类查不到
                    List<Field> objectField = getObjectField(o);
                    for (Field declaredField : objectField) {
                        mybatisConfig(declaredField, o);
                    }
                }
            } else {
                //获取当前类的所有字段向上最加一层，有可能是代理类查不到
                List<Field> objectField = getObjectField(v);
                for (Field declaredField : objectField) {
                    mybatisConfig(declaredField, v);
                }
            }
        });
        log.info("mybatis插件执行完成");
    }

    private void mybatisScan(Field declaredField, Set<Class<?>> classes) {
        //检查是否有注解@Autowired
        Autowired annotation = declaredField.getAnnotation(Autowired.class);
        if (annotation != null) {
            declaredField.setAccessible(true);
            //检查字段是类型是否被@Beetlsql标注
            Mybatis mybatis = declaredField.getType().getAnnotation(Mybatis.class);
            try {
                if (mybatis != null) {
                    classes.add(declaredField.getType());
                }
            } catch (Exception e) {
                log.error("装配错误");
                throw new RuntimeException(e);
            }
        }
    }

    private List<Field> getObjectField(Object clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> aClass = clazz.getClass();
        while (!aClass.equals(Object.class)) {
            Field[] declaredFields = aClass.getDeclaredFields();
            fields.addAll(Arrays.asList(declaredFields));
            aClass = aClass.getSuperclass();
        }
        return fields;
    }


    /**
     * Mybatis
     *
     * @param declaredField
     * @param v
     */
    private void mybatisConfig(Field declaredField, Object v) {
        //检查是否有注解@Autowired
        Autowired annotation = declaredField.getAnnotation(Autowired.class);
        if (annotation != null) {
            declaredField.setAccessible(true);
            //检查字段是类型是否被@Mybatis标注
            Mybatis mybatis = declaredField.getType().getAnnotation(Mybatis.class);
            try {
                if (mybatis != null) {
                    Object mapper = mybatisFlexBootstrap.getMapper(declaredField.getType());
                    //同类型注入
                    if (declaredField.getType().isAssignableFrom(mapper.getClass())) {
                        declaredField.set(v, mapper);
                        log.info("{}----->{}：装配完成，{}", mapper.getClass().getSimpleName(), v.getClass().getSimpleName(), "MybatisFlex注入");
                    } else {
                        log.error("{}----->{}：装配错误:类型不匹配", v.getClass().getSimpleName(), v.getClass().getSimpleName());
                    }
                }
            } catch (Exception e) {
                log.error("装配错误");
                throw new RuntimeException(e);
            }
        }
    }

}
