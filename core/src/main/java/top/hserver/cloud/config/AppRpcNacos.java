package top.hserver.cloud.config;

import lombok.Data;
import top.hserver.core.ioc.annotation.ConfigurationProperties;

@Data
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
}
