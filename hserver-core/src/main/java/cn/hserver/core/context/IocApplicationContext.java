package cn.hserver.core.context;

import cn.hserver.core.context.handler.AnnotationHandler;
import cn.hserver.core.ioc.bean.BeanDefinition;
import cn.hserver.core.ioc.BeanFactory;
import cn.hserver.core.aop.HookFactory;
import cn.hserver.core.plugin.PluginManager;
import cn.hserver.core.util.ClassLoadUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class IocApplicationContext {

    private final static BeanFactory beanFactory = new BeanFactory();

    private final Map<String, BeanDefinition> beanDefinitions = new HashMap<>();

    public IocApplicationContext(Set<String> basePackages) {
        // 扫描
        basePackages.forEach(this::scan);
        //处理aop、hook关系
        HookFactory.handlerHookData(beanDefinitions);
        PluginManager.getPlugin().iocStartRegister();
        registerBeanDefinition();
        PluginManager.getPlugin().iocStartPopulate();
        refresh();
    }

    public static void addBean(Object obj) {
        beanFactory.addBean(obj);
    }

    public static void addBean(String beanName,Object obj) {
        beanFactory.addBean(beanName,obj);
    }

    private void scan(String basePackage) {
        ClassLoadUtil.loadClasses(basePackage, false).forEach(this::processClass);
    }

    private void processClass(Class<?> clazz) {
        AnnotationHandler.ANNOTATION_HANDLERS.forEach(handler -> {
            handler.handle(clazz, beanDefinitions);
        });
        PluginManager.getPlugin().iocStartScan(clazz);
    }

    private void registerBeanDefinition(){
        // 注册所有Bean定义
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitions.entrySet()) {
            beanFactory.registerBeanDefinition(entry.getKey(), entry.getValue());
        }
    }

    private void refresh() {
        //单例bean的提前预处理
        // 新增：启动时预初始化所有单例Bean（处理依赖关系）
        for (String beanName : beanDefinitions.keySet()) {
            BeanDefinition beanDefinition = beanDefinitions.get(beanName);
            // 仅预初始化单例（默认作用域为singleton，若未指定Scope则视为singleton）
            if (beanDefinition.isSingleton()) {
                try {
                    // 调用getBean触发实例化和依赖注入，结果会缓存到beanFactory中
                    beanFactory.getBean(beanName);
                } catch (Exception e) {
                    throw new RuntimeException("初始化单例Bean失败：" + beanName, e);
                }
            }
        }
    }


    /**
     * 获取指定名称的Bean实例
     * @param name 要获取的Bean名称
     * @return 指定名称的Bean实例
     */
    public static Object getBean(String name) {
        try {
            return beanFactory.getBean(name);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 获取指定类型的Bean实例
     * @param requiredType 要获取的Bean类型
     * @param <T> Bean类型
     * @return 指定类型的Bean实例
     */
    public static  <T> T getBean(Class<T> requiredType){
        try {
            return beanFactory.getBean(requiredType);
        }catch (Exception e){
            return null;
        }
    }
    /**
     * 获取指定类型的所有Bean实例
     * @param requiredType 要获取的Bean类型
     * @param <T> Bean类型
     * @return 指定类型的所有Bean实例列表
     */
    public static  <T> List<T> getBeansOfType(Class<T> requiredType){
        return beanFactory.getBeansOfType(requiredType,false);
    }

    /**
     * 获取一个
     * @param requiredType
     * @return
     * @param <T>
     */
    public static  <T> T getBeansOfTypeOne(Class<T> requiredType){
        List<T> beansOfType = beanFactory.getBeansOfType(requiredType, false);
        if(!beansOfType.isEmpty()){
            return beansOfType.get(0);
        }
        return null;
    }

    /**
     * 获取排序后的bean
     * @param requiredType
     * @return
     * @param <T>
     */
    public static  <T> List<T> getBeansOfTypeSorted(Class<T> requiredType){
        return beanFactory.getBeansOfType(requiredType,true);
    }


    public static  <T> T getOrCreateRefreshTarget(BeanDefinition beanDefinition){
        try {
            return (T)beanFactory.getOrCreateRefreshTarget(beanDefinition);
        }catch (Exception e){
            return null;
        }
    }


    public static  void clearRefreshScope(){
       beanFactory.clearRefreshScope();
    }

}