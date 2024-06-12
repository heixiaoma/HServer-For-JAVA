package cn.hserver.core.ioc;


import javassist.util.proxy.ProxyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hxm
 */
public class HServerIoc implements Ioc {
    private static final Logger log = LoggerFactory.getLogger(HServerIoc.class);
    private static final Map<String, Object> POOL = new ConcurrentHashMap<>(32);

    @Override
    public Map<String, Object> getAll() {
        return POOL;
    }


    @Override
    public void addListBean(Object bean) {
        addListBean(bean.getClass().getName(), bean);
    }

    @Override
    public void addListBean(String name, Object bean) {
        if (name != null && name.trim().length() > 0 && bean != null) {
            Object o = POOL.computeIfAbsent(name, k -> new ArrayList<>());
            if (o instanceof List) {
                List o1 = (List) o;
                for (int i = 0; i < o1.size(); i++) {
                    //如果子类存在，父类直接跳过不被加入
                    if (bean.getClass().isAssignableFrom(o1.get(i).getClass()) || ProxyObject.class.isAssignableFrom(o1.get(i).getClass())) {
                        return;
                    }
                    //如果这个类是List中的子类，我们优先使用子类重写父类功能
                    if (o1.get(i).getClass().isAssignableFrom(bean.getClass())) {
                        o1.remove(i);
                    }
                }
                o1.add(bean);
            } else {
                log.warn("不是List");
            }
        }
    }

    @Override
    public <T> List<T> getListBean(Class<T> type) {
        Object o = POOL.get(type.getName());
        if (o instanceof List) {
            return (List) o;
        }
        return null;
    }

    @Override
    public List<Object> getListBean(String beanName) {
        Object o = POOL.get(beanName);
        if (o instanceof List) {
            return (List) o;
        }
        return null;
    }

    @Override
    public <T> T getBean(Class<T> type) {
        if (type != null) {
            Object o = POOL.get(type.getName());
            return type.cast(o);
        }
        return null;
    }

    @Override
    public <T> T getSupperBean(Class<T> type) {
        for (Object value : POOL.values()) {
            Class par = value.getClass();
            while (!par.equals(Object.class)) {
                //获取当前类的所有字段
                if (type.isAssignableFrom(par)) {
                    return type.cast(value);
                }
                par = par.getSuperclass();
            }
        }
        return null;
    }

    @Override
    public <T> List<T> getSupperBeanList(Class<T> type) {
        List<T> data = new ArrayList<>();
        for (Object value : POOL.values()) {
            Class par = value.getClass();
            while (!par.equals(Object.class)) {
                //获取当前类的所有字段
                if (type.isAssignableFrom(par)) {
                    data.add(type.cast(value));
                }
                par = par.getSuperclass();
            }
        }
        return data;
    }



    @Override
    public Object getBean(String beanName) {
        if (beanName != null && beanName.trim().length() > 0) {
            return POOL.get(beanName);
        }
        return null;
    }

    @Override
    public <T> T getBean(String beanName, Class<T> type) {
        try {
            if (type != null && beanName != null && beanName.trim().length() > 0) {
                Object o = POOL.get(beanName);
                return type.cast(o);
            }
        } catch (Exception e) {
            log.warn("{}转换异常{}", beanName, e.getMessage());
        }
        return null;
    }

    @Override
    public void addBean(Object bean) {
        if (bean != null) {
            String name = bean.getClass().getName();
            addBean(name, bean);
        }
    }

    @Override
    public void addBean(String name, Object bean) {
        if (name != null && name.trim().length() > 0 && bean != null) {

            Object bean1 = getBean(name);
            if (bean1 != null) {
                //如果子类存在，父类直接跳过不被加入 同时不是 代理类
                if (bean.getClass().isAssignableFrom(bean1.getClass()) || ProxyObject.class.isAssignableFrom(bean1.getClass())) {
                    return;
                }
            }
            POOL.put(name, bean);
        }
    }

    @Override
    public void remove(Class<?> type) {
        if (type != null) {
            String name = type.getName();
            POOL.remove(name);
        }
    }

    @Override
    public void remove(String beanName) {
        if (beanName != null) {
            POOL.remove(beanName);
        }
    }

    @Override
    public void clearAll() {
        POOL.clear();
    }

    @Override
    public boolean exist(String beanName) {
        if (beanName != null && beanName.trim().length() > 0) {
            if (POOL.containsKey(beanName)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean exist(Class<?> type) {
        if (type != null) {
            String name = type.getName();
            if (POOL.containsKey(name)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
