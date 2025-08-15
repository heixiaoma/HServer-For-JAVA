package cn.hserver.core.config;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hxm
 */
public class ConfigMap extends ConcurrentHashMap<String, Object> {

        @Override
        public Object put(String key, Object value) {
            return super.put(key.toLowerCase(), value);
        }

        public Object get(String key) {
            return super.get(key.toLowerCase());
        }

        @Override
        public Object get(Object key) {
            return super.get(key.toString().toLowerCase());
        }

        @Override
        public Object getOrDefault(Object key, Object defaultValue) {
            return super.getOrDefault(key.toString().toLowerCase(), defaultValue);
        }
}
