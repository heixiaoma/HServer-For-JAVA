package cn.hserver.cloud.nacos;

import cn.hserver.cloud.common.RegisterConfig;
import cn.hserver.cloud.register.RegisterService;
import cn.hserver.core.ioc.annotation.Autowired;
import cn.hserver.core.ioc.annotation.Component;
import cn.hserver.core.life.CloseAdapter;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Component
public class NacosRegisterService implements RegisterService, CloseAdapter {

    private static final Logger log = LoggerFactory.getLogger(NacosRegisterService.class);

    @Autowired
    private NamingService naming;

    private RegisterConfig regProp;

    @Override
    public boolean register(RegisterConfig regProp) {
        this.regProp = regProp;
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

    @Override
    public void close() {
        this.deregister();
    }
}
