package top.hserver.cloud.config;

import top.hserver.core.ioc.annotation.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rpc.nacos")
public class AppRpcNacos {
    /**
     * nacos 地址
     */
    private String address;


    /**
     * 服务名字
     */
    private String name;

    /**
     * 服务组名
     */
    private String group;

    /**
     * 能访问到自己的IP地址，nacos 才用
     */
    private String ip;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
