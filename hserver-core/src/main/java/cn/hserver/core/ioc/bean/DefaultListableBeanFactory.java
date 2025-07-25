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
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

    @Override
    public Object getBean(String name) throws Exception {
        BeanDefinition beanDefinition = beanDefinitionMap.get(name);
        if (beanDefinition == null) {
            throw new IllegalArgumentException("No bean named '" + name + "' found");
        }

        if (beanDefinition.isSingleton()) {
            Object singleton = singletonObjects.get(name);
            if (singleton == null) {
                singleton = createBean(beanDefinition);
                singletonObjects.put(name, singleton);
            }
            return singleton;
        } else {
            return createBean(beanDefinition);
        }
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
            constructorToUse = (autowiredConstructor != null) ? autowiredConstructor : beanClass.getDeclaredConstructor();
        }

        // 处理构造器参数
        Object[] args = resolveConstructorArguments(constructorToUse);

        // 创建实例
        Object beanInstance = constructorToUse.newInstance(args);

        // 处理字段注入
        injectFields(beanInstance);

        return beanInstance;
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

    private void injectFields(Object beanInstance) throws Exception {
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
}    