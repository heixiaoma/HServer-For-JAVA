package top.hserver.core.ioc;


import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hxm
 */
@Slf4j
public class HServerIoc implements Ioc {

    private static final Map<String, Object> POOL = new ConcurrentHashMap<>(32);

    @Override
    public Map<String, Object> getAll() {
        return POOL;
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
            log.warn("{}转换异常{}",beanName, e.getMessage());
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
        if (name != null && name.trim().length() > 0 && bean != null ) {
            POOL.put(name, bean);
        }
    }

    @Override
    public void remove(Class<?> type) {
        if (type != null) {
            String name = type.getName();
            if (POOL.containsKey(name)) {
                POOL.remove(name);
            }
        }
    }

    @Override
    public void remove(String beanName) {
        if (beanName != null) {
            if (POOL.containsKey(beanName)) {
                POOL.remove(beanName);
            }
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