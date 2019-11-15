package com.hserver.core.ioc;


import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class HookIoc implements Ioc {

    private static final Map<String, Object> pool = new ConcurrentHashMap<>(32);

    @Override
    public Map<String, Object> getAll() {
        return pool;
    }

    @Override
    public <T> T getBean(Class<T> type) {
        if (type != null) {
            Object o = pool.get(type.getName());
            return type.cast(o);
        }
        return null;
    }

    @Override
    public Object getBean(String beanName) {
        if (beanName != null && beanName.trim().length() > 0) {
            return pool.get(beanName);
        }
        return null;
    }

    @Override
    public <T> T getBean(String beanName, Class<T> type) {
        try {
            if (type != null && beanName != null && beanName.trim().length() > 0) {
                Object o = pool.get(beanName);
                return type.cast(o);
            }
        } catch (Exception e) {
            log.warn(beanName + "转换异常" + e.getMessage());
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
            pool.put(name, bean);
        }
    }

    @Override
    public void remove(Class<?> type) {
        if (type != null) {
            String name = type.getName();
            if (pool.containsKey(name)) {
                pool.remove(name);
            }
        }
    }

    @Override
    public void remove(String beanName) {
        if (beanName != null) {
            if (pool.containsKey(beanName)) {
                pool.remove(beanName);
            }
        }
    }

    @Override
    public void clearAll() {
        pool.clear();

    }

    @Override
    public boolean exist(String beanName) {
        if (beanName != null && beanName.trim().length() > 0) {
            if (pool.containsKey(beanName)) {
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
            if (pool.containsKey(name)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}