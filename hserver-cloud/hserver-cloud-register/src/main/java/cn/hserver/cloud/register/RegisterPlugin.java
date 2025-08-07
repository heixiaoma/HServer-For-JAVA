package cn.hserver.cloud.register;

import cn.hserver.cloud.common.RegProp;
import cn.hserver.core.config.ConfigData;
import cn.hserver.core.config.ConstConfig;
import cn.hserver.core.context.IocApplicationContext;
import cn.hserver.core.plugin.bean.PluginInfo;
import cn.hserver.core.plugin.handler.PluginAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterPlugin extends PluginAdapter {
    private static final Logger log = LoggerFactory.getLogger(RegisterPlugin.class);

    @Override
    public PluginInfo getPluginInfo() {
        return new PluginInfo.Builder()
                .name("服务注册")
                .description("提供服务注册能力，让服务发现模块能够快速感知并实现服务调用")
                .build();
    }

    @Override
    public void startedApp() {
        RegisterService beansOfTypeOne = IocApplicationContext.getBeansOfTypeOne(RegisterService.class);
        if (beansOfTypeOne == null) {
            log.warn("未找到注册服务实现类");
            return;
        }
        RegProp regProp = IocApplicationContext.getBean(RegProp.class);
        if (regProp == null) {
            regProp = new RegProp();
            regProp.setRegisterMyIp(ConfigData.getInstance().getString("cloud.register.ip"));
            regProp.setRegisterMyPort(ConfigData.getInstance().getInteger("cloud.register.port"));
            regProp.setRegisterName(ConfigData.getInstance().getString("cloud.register.name"));
            regProp.setRegisterGroupName(ConfigData.getInstance().getString("cloud.register.group"));
            regProp.setRegisterAddress(ConfigData.getInstance().getString("cloud.register.address"));
        }
        if (regProp.hasNull()) {
            log.warn("注册服务配置错误");
            return;
        }
        beansOfTypeOne.register(regProp);
    }
}
