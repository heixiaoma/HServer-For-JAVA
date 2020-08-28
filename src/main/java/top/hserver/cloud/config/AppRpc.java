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
    private String mode;

    /**
     * 消费者还是提供者
     * true 消费者
     * false 提供者
     */
    private boolean type;

    /**
     * 消费者连接提供者的地址
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

}
