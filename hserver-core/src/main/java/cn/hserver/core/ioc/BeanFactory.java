package cn.hserver.core.ioc;

import cn.hserver.core.aop.HookFactory;
import cn.hserver.core.config.ConfigData;
import cn.hserver.core.config.annotation.ConfigurationProperties;
import cn.hserver.core.config.annotation.Value;
import cn.hserver.core.ioc.annotation.Autowired;
import cn.hserver.core.ioc.annotation.Qualifier;
import cn.hserver.core.ioc.annotation.PostConstruct;
import cn.hserver.core.ioc.bean.BeanDefinition;
import cn.hserver.core.ioc.handler.PopulateBeanHandler;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BeanFactory {

    private HookFactory hookFactory=new HookFactory();

    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    // 一级缓存：存储完全初始化的单例Bean
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();
    // 二级缓存：存储早期曝光的单例Bean（未完全初始化）
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>();
    // 三级缓存：存储ObjectFactory，用于创建早期Bean引用
    private final Map<String,Object> singletonFactories = new ConcurrentHashMap<>();
    // 记录正在创建的单例Bean，用于检测循环依赖
    private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public Object getBean(String name) throws Exception {
        return doGetBean(name);
    }

    public <T> T getBean(Class<T> requiredType) throws Exception {
        List<String> beanNames = new ArrayList<>();
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            if (requiredType.isAssignableFrom(entry.getValue().getBeanClass())) {
                beanNames.add(entry.getKey());
                break;
            }
        }
        if (beanNames.isEmpty()) {
            throw new IllegalArgumentException("No qualifying bean of type '" + requiredType.getName() + "' found");
        }
        return requiredType.cast(getBean(beanNames.get(0)));
    }


    private <T> T doGetBean(String name) throws Exception {
        // 从一级缓存获取（完全初始化的Bean）
        Object sharedInstance = singletonObjects.get(name);
        if (sharedInstance != null) {
            return (T) sharedInstance;
        }
        // 检查是否正在创建（处理循环依赖）
        if (singletonsCurrentlyInCreation.contains(name)) {
            // 尝试从二级缓存获取早期曝光的Bean
            sharedInstance = earlySingletonObjects.get(name);
            if (sharedInstance != null) {
                return (T) sharedInstance;
            }
            // 尝试从三级缓存获取ObjectFactory
            Object object = singletonFactories.get(name);
            if (object != null) {
                sharedInstance = object;
                // 将早期曝光的Bean放入二级缓存
                earlySingletonObjects.put(name, sharedInstance);
                // 从三级缓存移除
                singletonFactories.remove(name);
                return (T) sharedInstance;
            }
        }
        BeanDefinition beanDefinition = beanDefinitionMap.get(name);
        if (beanDefinition == null) {
            throw new IllegalArgumentException("No bean named '" + name + "' found");
        }

        if (beanDefinition.isSingleton()) {
            synchronized (singletonObjects) {
                sharedInstance = singletonObjects.get(name);
                if (sharedInstance == null) {
                    singletonsCurrentlyInCreation.add(name);

                    try {
                        final Object beanInstance = createBean(beanDefinition);
                        singletonFactories.put(name, beanInstance);
                        populateBean(beanInstance);
                        if (!singletonObjects.containsKey(name)){
                            // 新增：执行@PostConstruct注解方法
                            initializeBean(beanInstance);
                        }
                        singletonObjects.put(name, beanInstance);
                        earlySingletonObjects.remove(name);
                        singletonFactories.remove(name);
                        singletonsCurrentlyInCreation.remove(name);
                        return (T) beanInstance;
                    } catch (Exception ex) {
                        singletonsCurrentlyInCreation.remove(name);
                        throw ex;
                    }
                }
            }
        } else if (beanDefinition.isPrototype()) {
            Object prototypeInstance = createBean(beanDefinition);
            // 新增：执行@PostConstruct注解方法
            initializeBean(prototypeInstance);
            return (T) prototypeInstance;
        } else {
            throw new IllegalArgumentException("Unsupported scope: " + beanDefinition.getScope());
        }

        return (T) sharedInstance;
    }

    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    private Object createBean(BeanDefinition beanDefinition) throws Exception {
        // 新增：支持工厂方法创建Bean
        if (beanDefinition.getFactoryMethod() != null) {
            Object factoryBean = getBean(beanDefinition.getFactoryBeanName());
            Method factoryMethod = beanDefinition.getFactoryMethod();
            Object[] args = resolveMethodArguments(factoryMethod);
            return factoryMethod.invoke(factoryBean, args);
        }

        Class<?> beanClass = beanDefinition.getBeanClass();
        Constructor<?> constructorToUse = beanDefinition.getConstructor();

        // 如果没有指定构造器，尝试查找带有@Autowired注解的构造器
        if (constructorToUse == null) {
            Constructor<?>[] constructors = beanClass.getDeclaredConstructors();
            Constructor<?> autowiredConstructor = null;

            for (Constructor<?> constructor : constructors) {
                if (constructor.isAnnotationPresent(Autowired.class)) {
                    if (autowiredConstructor != null) {
                        throw new IllegalArgumentException("Multiple constructors with @Autowired found in " + beanClass);
                    }
                    autowiredConstructor = constructor;
                }
            }

            // 如果没有找到@Autowired注解的构造器，使用默认无参构造器
            constructorToUse = (autowiredConstructor != null) ? autowiredConstructor :
                    constructors[0]; // 确保使用无参构造器
        }

        // 处理构造器参数
        Object[] args = resolveConstructorArguments(constructorToUse);

        Object beanInstance;
        if (beanDefinition.isHook()){
            beanInstance= hookFactory.newProxyInstance(beanDefinition,constructorToUse,args);
        }else {
            beanInstance = constructorToUse.newInstance(args);
        }
        // 记录构造器，避免重复解析
        beanDefinition.setConstructor(constructorToUse);
        return beanInstance;
    }


    /**
     * 注入实体数据
     * @param beanInstance
     * @throws Exception
     */
    private void populateBean(Object beanInstance) throws Exception {
        for (PopulateBeanHandler handler : PopulateBeanHandler.HANDLERS) {
            handler.populate(beanInstance);
        }
    }

    private Object[] resolveConstructorArguments(Constructor<?> constructor) throws Exception {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] args = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            String beanName = null;
            // 检查参数上是否有@Qualifier注解
            Parameter parameter = constructor.getParameters()[i];
            Qualifier qualifier = parameter.getAnnotation(Qualifier.class);
            if (qualifier != null) {
                beanName = qualifier.value();
            }

            if (beanName != null) {
                args[i] = getBean(beanName);
            } else {
                args[i] = getBean(parameterType);
            }
        }

        return args;
    }

    // 新增：解析方法参数依赖注入
    private Object[] resolveMethodArguments(Method method) throws Exception {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] args = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            String beanName = null;
    
            // 处理@Qualifier注解
            Parameter parameter = method.getParameters()[i];
            Qualifier qualifier = parameter.getAnnotation(Qualifier.class);
            if (qualifier != null) {
                beanName = qualifier.value();
            }
    
            if (beanName != null) {
                args[i] = getBean(beanName);
            } else {
                args[i] = getBean(parameterType);
            }
        }
        return args;
    }

    // 新增：初始化Bean，执行@PostConstruct方法
    private void initializeBean(Object beanInstance) throws Exception {
        Class<?> beanClass = beanInstance.getClass();
        Method postConstructMethod = null;

        // 查找@PostConstruct注解的方法
        for (Method method : beanClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                if (postConstructMethod != null) {
                    throw new IllegalStateException("Multiple @PostConstruct methods found in " + beanClass.getName());
                }
                if (method.getParameterCount() != 0) {
                    throw new IllegalArgumentException("@PostConstruct method " + method.getName() + " must have no parameters");
                }
                postConstructMethod = method;
            }
        }

        // 执行@PostConstruct方法
        if (postConstructMethod != null) {
            postConstructMethod.setAccessible(true);
            postConstructMethod.invoke(beanInstance);
        }
    }
}