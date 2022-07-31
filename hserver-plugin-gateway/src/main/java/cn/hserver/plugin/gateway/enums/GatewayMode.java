package cn.hserver.plugin.gateway.enums;

public enum GatewayMode {
    HTTP("http"),
    TCP("tcp"),
    ;

    private final String desc;

    GatewayMode(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static GatewayMode getMode(String desc) {
        for (GatewayMode value : GatewayMode.values()) {
            if (value.desc.equals(desc)) {
                return value;
            }
        }
        return null;
    }

}
