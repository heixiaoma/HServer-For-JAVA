package cn.hserver.plugin.web.context;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hxm
 */
public class HeadMap extends ConcurrentHashMap<String, String> {

        @Override
        public String put(String key, String value) {
            return super.put(key.toLowerCase(), value);
        }

        public String get(String key) {
            return super.get(key.toLowerCase());
        }
    }
