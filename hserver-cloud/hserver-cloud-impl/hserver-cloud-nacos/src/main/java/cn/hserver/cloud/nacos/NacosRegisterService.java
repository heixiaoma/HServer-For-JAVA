package cn.hserver.cloud.nacos;

import cn.hserver.cloud.common.RegProp;
import cn.hserver.cloud.register.RegisterService;
import cn.hserver.core.ioc.annotation.Component;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Component
public class NacosRegisterService implements RegisterService {

    private static final Logger log = LoggerFactory.getLogger(NacosRegisterService.class);

    private NamingService naming;

    private RegProp regProp;

    @Override
    public boolean register(RegProp regProp) {
        this.regProp = regProp;
        try {
            naming = NamingFactory.createNamingService(regProp.getRegisterAddress());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        try {
            //注册服务
            naming.registerInstance(
                    regProp.getRegisterName(),
                    regProp.getRegisterGroupName(),
                    regProp.getRegisterMyIp(),
                    regProp.getRegisterMyPort(),
                    regProp.getRegisterName()
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean deregister() {
        try {
            naming.deregisterInstance(regProp.getRegisterName(), regProp.getRegisterGroupName(), regProp.getRegisterMyIp(), regProp.getRegisterMyPort());
        } catch (NacosException e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

}
