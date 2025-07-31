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
    }
