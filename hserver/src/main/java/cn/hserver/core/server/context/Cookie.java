package cn.hserver.core.server.context;

import java.util.HashMap;

/**
 * @author hxm
 */
public class Cookie extends HashMap<String, String> {

    private String path;

    private Integer maxAge;

    public Cookie add(String key, String value) {
        super.put(key, value);
        return this;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }
}
