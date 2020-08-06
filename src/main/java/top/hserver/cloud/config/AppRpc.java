package top.hserver.cloud.config;

import lombok.Data;
import top.hserver.core.ioc.annotation.ConfigurationProperties;

/**
 * RPC基础配置信息
 */
@Data
@ConfigurationProperties(prefix = "app.rpc")
public class AppRpc {

    /**
     * 是否开启Rpc
     */
    private boolean open;


    /**
     * nacos 或者默认模式
     */
    private String type;


    /**
     * 消费者连接提供者的地址
     */

    private String address;

}
