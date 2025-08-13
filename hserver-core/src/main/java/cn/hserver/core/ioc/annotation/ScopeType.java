package cn.hserver.core.ioc.annotation;

public enum ScopeType {
    SINGLETON("singleton"),
    PROTOTYPE("prototype"),
    REFRESH("refresh");

    private final String value;

    ScopeType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
