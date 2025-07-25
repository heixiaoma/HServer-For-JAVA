package cn.hserver.core.ioc.bean;

import cn.hserver.core.ioc.annotation.Autowired;
import cn.hserver.core.ioc.annotation.Qualifier;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultListableBeanFactory implements BeanFactory {
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    // 一级缓存：存储完全初始化的单例Bean
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();
    // 二级缓存：存储早期曝光的单例Bean（未完全初始化）
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>();
    // 三级缓存：存储ObjectFactory，用于创建早期Bean引用
    private final Map<String, ObjectFactory<?>> singletonFactories = new ConcurrentHashMap<>();
    // 记录正在创建的单例Bean，用于检测循环依赖
    private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public Object getBean(String name) throws Exception {
        return doGetBean(name, null);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws Exception {
        List<String> beanNames = new ArrayList<>();
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            if (requiredType.isAssignableFrom(entry.getValue().getBeanClass())) {
                beanNames.add(entry.getKey());
            }
        }

        if (beanNames.isEmpty()) {
            throw new IllegalArgumentException("No qualifying bean of type '" + requiredType.getName() + "' found");
        }

        if (beanNames.size() > 1) {
            throw new IllegalArgumentException("Expected single matching bean but found " + beanNames.size() + ": " + beanNames);
        }

        return requiredType.cast(getBean(beanNames.get(0)));
    }

    private <T> T doGetBean(String name, Class<T> requiredType) throws Exception {
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
            ObjectFactory<?> factory = singletonFactories.get(name);
            if (factory != null) {
                sharedInstance = factory.getObject();
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
            // 单例模式下，使用同步块确保线程安全
            synchronized (singletonObjects) {
                sharedInstance = singletonObjects.get(name);
                if (sharedInstance == null) {
                    // 标记为正在创建
                    singletonsCurrentlyInCreation.add(name);

                    try {
                        // 创建Bean实例（但不填充属性）
                        final Object beanInstance = createBeanInstance(beanDefinition);

                        // 提前曝光：将Bean放入三级缓存
                        singletonFactories.put(name, () -> beanInstance);

                        // 填充属性（可能触发循环依赖）
                        populateBean(beanInstance, beanDefinition);

                        // 初始化完成：放入一级缓存
                        singletonObjects.put(name, beanInstance);

                        // 从二级、三级缓存和创建中集合移除
                        earlySingletonObjects.remove(name);
                        singletonFactories.remove(name);
                        singletonsCurrentlyInCreation.remove(name);

                        return (T) beanInstance;
                    } catch (Exception ex) {
                        // 创建失败，清理状态
                        singletonsCurrentlyInCreation.remove(name);
                        throw ex;
                    }
                }
            }
        } else if (beanDefinition.isPrototype()) {
            // 原型模式：直接创建新实例（不支持循环依赖）
            Object prototypeInstance = createBean(beanDefinition);
            return (T) prototypeInstance;
        } else {
            throw new IllegalArgumentException("Unsupported scope: " + beanDefinition.getScope());
        }

        return (T) sharedInstance;
    }

    @Override
    public boolean containsBean(String name) {
        return beanDefinitionMap.containsKey(name);
    }

    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    private Object createBean(BeanDefinition beanDefinition) throws Exception {
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

        // 创建实例
        Object beanInstance = constructorToUse.newInstance(args);

        // 记录构造器，避免重复解析
        beanDefinition.setConstructor(constructorToUse);

        return beanInstance;
    }

    private Object createBeanInstance(BeanDefinition beanDefinition) throws Exception {
        // 简化版：直接调用createBean，但实际应只创建实例不填充属性
        return createBean(beanDefinition);
    }

    private void populateBean(Object beanInstance, BeanDefinition beanDefinition) throws Exception {
        Class<?> beanClass = beanInstance.getClass();
        Field[] fields = beanClass.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Object fieldValue = null;
                String beanName = null;

                // 检查字段上是否有@Qualifier注解
                Qualifier qualifier = field.getAnnotation(Qualifier.class);
                if (qualifier != null) {
                    beanName = qualifier.value();
                }

                // 1. 优先尝试通过setter方法注入
                try {
                    // 使用Introspector获取BeanInfo
                    BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
                    PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

                    // 查找与字段匹配的属性描述符
                    for (PropertyDescriptor pd : propertyDescriptors) {
                        if (pd.getName().equals(field.getName()) && pd.getWriteMethod() != null) {
                            Method setterMethod = pd.getWriteMethod();

                            // 确保setter方法是public的
                            if (Modifier.isPublic(setterMethod.getModifiers())) {
                                // 获取要注入的bean实例
                                if (beanName != null) {
                                    fieldValue = getBean(beanName);
                                } else {
                                    fieldValue = getBean(field.getType());
                                }

                                // 调用setter方法
                                setterMethod.invoke(beanInstance, fieldValue);
                                continue; // 成功通过setter注入，跳过字段反射
                            }
                        }
                    }
                } catch (Exception e) {
                    // 忽略异常，继续尝试字段反射注入
                }

                // 2. 使用字段反射注入
                field.setAccessible(true);
                if (beanName != null) {
                    fieldValue = getBean(beanName);
                } else {
                    fieldValue = getBean(field.getType());
                }
                field.set(beanInstance, fieldValue);
            }
        }
    }

    private Object[] resolveConstructorArguments(Constructor<?> constructor) throws Exception {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] args = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            String beanName = null;

            // 检查参数上是否有@Qualifier注解
            java.lang.reflect.Parameter parameter = constructor.getParameters()[i];
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
}