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
     * nacos 或者默认模式
     */
    private String mode;

    /**
     * 消费者连接提供者的地址
     */

    private String address;


}
