package cn.hserver.plugin.satoken;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.config.SaTokenConfigFactory;
import cn.dev33.satoken.context.SaTokenContext;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.same.SaSameTemplate;
import cn.dev33.satoken.sign.SaSignTemplate;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.temp.SaTempInterface;
import cn.hserver.core.interfaces.PluginAdapter;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.ref.PackageScanner;
import cn.hserver.plugin.satoken.config.SaTokenContextForHServer;
import cn.hserver.plugin.web.WebPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class SaTokenPlugin implements PluginAdapter {
    private static final Logger log = LoggerFactory.getLogger(SaTokenPlugin.class);

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

    }

    @Override
    public void iocInitEnd() {

    }

    @Override
    public void startInjection() {

    }

    @Override
    public void injectionEnd() {
        SaTokenContext saTokenContext = new SaTokenContextForHServer();
        SaManager.setSaTokenContext(saTokenContext);
        StpInterface stpInterface = IocUtil.getSupperBean(StpInterface.class);
        if (stpInterface != null) {
            SaManager.setStpInterface(stpInterface);
        }
        SaTokenDao saTokenDao = IocUtil.getSupperBean(SaTokenDao.class);
        if (saTokenDao != null) {
            SaManager.setSaTokenDao(saTokenDao);
        }

        SaSignTemplate saSignTemplate = IocUtil.getSupperBean(SaSignTemplate.class);
        if (saSignTemplate != null){
            SaManager.setSaSignTemplate(saSignTemplate);
        }

        SaTempInterface saTempInterface = IocUtil.getSupperBean(SaTempInterface.class);
        if (saTempInterface != null){
            SaManager.setSaTemp(saTempInterface);
        }
        SaJsonTemplate saJsonTemplate = IocUtil.getSupperBean(SaJsonTemplate.class);
        if (saJsonTemplate != null){
            SaManager.setSaJsonTemplate(saJsonTemplate);
        }

        SaSameTemplate saSameTemplate = IocUtil.getSupperBean(SaSameTemplate.class);
        if (saSameTemplate != null){
            SaManager.setSaSameTemplate(saSameTemplate);
        }

        log.info("Sa-Token插件启动成功");
    }
}
