package top.hserver.cloud.config;

import top.hserver.core.ioc.annotation.ConfigurationProperties;

/**
 * RPC基础配置信息
 */
@ConfigurationProperties(prefix = "app.rpc")
public class AppRpc {

    /**
     * nacos 或者默认模式
     */
    private String mode;

    /**
     * 消费者连接提供者的地址
     */

    private String address;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
