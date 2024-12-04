package cn.hserver.plugin.forest;

import cn.hserver.core.interfaces.PluginAdapter;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.ref.PackageScanner;
import cn.hserver.plugin.forest.config.ForestClientConfig;
import com.dtflys.forest.Forest;
import com.dtflys.forest.annotation.ForestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ForestPlugin implements PluginAdapter {
    private static final Logger log = LoggerFactory.getLogger(ForestPlugin.class);

    @Override
    public void startApp() {

    }

    @Override
    public void startIocInit() {

    }

    @Override
    public Set<Class<?>> iocInitBeanList() {
        return null;
    }

    @Override
    public void iocInit(PackageScanner packageScanner) {
        try {
            Set<Class<?>> annotationList = packageScanner.getAnnotationList(ForestClient.class);
            for (Class<?> aClass : annotationList) {
                Object data = Forest.client(aClass);
                if (data != null) {
                    IocUtil.addBean(data);
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void iocInitEnd() {

    }

    @Override
    public void startInjection() {

    }

    @Override
    public void injectionEnd() {
        ForestClientConfig supperBean = IocUtil.getSupperBean(ForestClientConfig.class);
        if (supperBean != null) {
            supperBean.config(Forest.config());
        }
        log.info("Forest插件执行完成");
    }
}
