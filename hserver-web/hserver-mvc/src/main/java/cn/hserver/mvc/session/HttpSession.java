package cn.hserver.mvc.session;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {

    private final Map<String, Object> attributes = new HashMap<>();

    private String id = null;

    private long created = -1;

    private long expired = -1;

    public <T> T attribute(String name) {
        Object object = this.attributes.get(name);
        return null != object ? (T) object : null;
    }

    public void attribute(String name, Object value) {
        this.attributes.put(name, value);
    }

    public Map<String, Object> attributes() {
        return attributes;
    }

    public void removeAttribute(String name) {
        this.attributes.remove(name);
    }

    public String id() {
        return id;
    }

    public void id(String id) {
        this.id = id;
    }

    public long created() {
        return this.created;
    }

    public void created(long created) {
        this.created = created;
    }

    public long expired() {
        return this.expired;
    }

    public void expired(long expired) {
        this.expired = expired;
    }

    

}
