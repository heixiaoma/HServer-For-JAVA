package cn.hserver.cloud.nacos;

import cn.hserver.cloud.common.CloudAddress;
import cn.hserver.cloud.common.ConstConfig;
import cn.hserver.core.config.ConfigData;
import cn.hserver.core.config.annotation.Configuration;
import cn.hserver.core.context.IocApplicationContext;
import cn.hserver.core.ioc.annotation.Bean;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class NamingConfig {
    private static final Logger log = LoggerFactory.getLogger(NamingConfig.class);

    @Bean
    public NamingService namingService() {
        CloudAddress cloudAddress = IocApplicationContext.getBeansOfTypeOne(CloudAddress.class);
        if (cloudAddress==null) {
            String string = ConfigData.getInstance().getString(ConstConfig.CLOUD_ADDRESS,null);
            cloudAddress=new CloudAddress();
            cloudAddress.setCloudAddress(string);
        }
        try {
            return NamingFactory.createNamingService(cloudAddress.getCloudAddress());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
