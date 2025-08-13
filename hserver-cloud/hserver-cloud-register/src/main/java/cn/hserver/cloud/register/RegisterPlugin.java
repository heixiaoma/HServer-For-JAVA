package cn.hserver.cloud.register;

import cn.hserver.cloud.common.ConstConfig;
import cn.hserver.cloud.common.RegisterConfig;
import cn.hserver.core.config.ConfigData;
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
        //服务注册
        RegisterConfig regProp = IocApplicationContext.getBean(RegisterConfig.class);
        if (regProp == null) {
            regProp = new RegisterConfig();
            regProp.setRegisterMyIp(ConfigData.getInstance().getString(ConstConfig.REGISTER_MY_IP));
            regProp.setRegisterMyPort(ConfigData.getInstance().getInteger(ConstConfig.REGISTER_MY_PORT));
            regProp.setRegisterName(ConfigData.getInstance().getString(ConstConfig.REGISTER_NAME));
            regProp.setRegisterGroupName(ConfigData.getInstance().getString(ConstConfig.REGISTER_GROUP_NAME,ConstConfig.DEFAULT_GROUP_NAME));
            regProp.setCloudAddress(ConfigData.getInstance().getString(ConstConfig.CLOUD_ADDRESS));
        }
        if (!regProp.hasNull()) {
            beansOfTypeOne.register(regProp);
        }
    }
}
