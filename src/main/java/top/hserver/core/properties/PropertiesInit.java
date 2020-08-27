package top.hserver.core.properties;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import top.hserver.HServerApplication;
import top.hserver.core.ioc.IocUtil;
import top.hserver.core.ioc.annotation.nacos.NacosClass;
import top.hserver.core.ioc.annotation.nacos.NacosValue;
import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.context.HeadMap;
import top.hserver.core.server.util.ClassLoadUtil;
import top.hserver.core.server.util.NamedThreadFactory;
import top.hserver.core.server.util.ParameterUtil;
import top.hserver.core.server.util.PropUtil;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.Executor;

/**
 * @author hxm
 */

@Slf4j
public class PropertiesInit {


    private static final Listener listener = new Listener() {
        @Override
        public void receiveConfigInfo(String configInfo) {
            //重启项目
            HServerApplication.reInitIoc();
        }

        @Override
        public Executor getExecutor() {
            return null;
        }
    };

    private static ConfigService configService;

    public static void configFile(Set<String> scanPackage) {
        PropUtil instance = PropUtil.getInstance();
        Integer taskPool = instance.getInt("taskPool");
        if (taskPool != null) {
            ConstConfig.taskPool = taskPool;
        }
        Integer queuePool = instance.getInt("queuePool");
        if (taskPool != null) {
            ConstConfig.queuePool = queuePool;
        }
        Integer bossPool = instance.getInt("bossPool");
        if (bossPool != null) {
            ConstConfig.bossPool = bossPool;
        }
        Integer workerPool = instance.getInt("workerPool");
        if (workerPool != null) {
            ConstConfig.workerPool = workerPool;
        }
        ConstConfig.EPOLL = Boolean.valueOf(instance.get("epoll"));
        Integer businessPool = instance.getInt("businessPool");
        if (businessPool != null) {
            ConstConfig.BUSINESS_EVENT = new DefaultEventExecutorGroup(businessPool, new NamedThreadFactory("hserver_business"));
        }

        String config = instance.get("app.nacos.config.address");

        if (config != null) {
            log.info("初始化配置中心");
            nacosConfig(config, scanPackage);
            log.info("初始化配置中心完成");
        }
    }

    private static void nacosConfig(String config, Set<String> scanPackage) {

        Map<Class, NacosClass> nacosClasss = new HashMap<>();
        Map<Field, NacosValue> nacosValues = new HashMap<>();

        scanPackage.forEach(packagePath -> {
            List<Class<?>> classes = ClassLoadUtil.LoadClasses(packagePath, false);
            for (Class<?> aClass : classes) {
                //类级别的注解
                NacosClass nacosClass = aClass.getAnnotation(NacosClass.class);
                if (nacosClass != null) {
                    nacosClasss.put(aClass, nacosClass);
                }
                //字段级别注解
                for (Field field : aClass.getDeclaredFields()) {
                    NacosValue nacosValue = field.getAnnotation(NacosValue.class);
                    if (nacosValue != null) {
                        nacosValues.put(field, nacosValue);
                    }
                }
            }
        });
        if (nacosClasss.size() > 0 || nacosValues.size() > 0) {
            try {
                if (configService == null) {
                    configService = NacosFactory.createConfigService(config);
                }
                /**
                 * 处理类级别
                 */
                nacosClasss.forEach((k, v) -> {
                    try {
                        Object value;
                        String content = configService.getConfig(v.dataId(), v.group(), 5000);
                        //是否是json 类型
                        value = toJSon(content, k);
                        //是否是Properties类型
                        if (value == null) {
                            value = toProperties(content, k);
                        }
                        //其他类型带扩展.....
                        if (value != null) {
                            IocUtil.addBean(value);
                        }
                        configService.removeListener(v.dataId(), v.group(), listener);
                        configService.addListener(v.dataId(), v.group(), listener);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error(e.getMessage());
                    }
                });
                /**
                 * 处理字段级别
                 */
                nacosValues.forEach((k, v) -> {
                    try {
                        String dataId = v.dataId();
                        String group = v.group();
                        Object bean = IocUtil.getBean(dataId + group);
                        if (bean == null) {
                            String content = configService.getConfig(dataId, group, 5000);
                            Object convert = ParameterUtil.convert(k, content);
                            IocUtil.addBean(dataId + group, convert);
                            configService.addListener(dataId, group, listener);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error(e.getMessage());
                    }
                });
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }


    private static Object toJSon(String content, Class classy) {
        try {
            return ConstConfig.JSON.readValue(content, classy);
        } catch (Exception ignored) {
        }
        return null;
    }

    private static Object toProperties(String content, Class classy) {
        try {
            HeadMap headMap = new HeadMap();
            Properties properties = new Properties();
            StringReader stringReader = new StringReader(content);
            properties.load(stringReader);
            properties.forEach((k, v) -> headMap.put(k.toString(), v.toString()));
            properties.clear();
            stringReader.close();
            if (headMap.size() == 0) {
                return null;
            }
            Object o = classy.newInstance();
            for (Field field : classy.getDeclaredFields()) {
                field.setAccessible(true);
                field.set(o, ParameterUtil.convert(field, headMap.get(field.getName())));
            }
            return o;
        } catch (Exception ignored) {
        }
        return null;
    }

}
