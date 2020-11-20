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
}
