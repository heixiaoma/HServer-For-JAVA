package cn.hserver.plugin.satoken;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.handler.SaAnnotationHandlerInterface;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaTokenContext;
import cn.dev33.satoken.context.second.SaTokenSecondContextCreator;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.httpauth.basic.SaHttpBasicTemplate;
import cn.dev33.satoken.httpauth.basic.SaHttpBasicUtil;
import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.listener.SaTokenEventCenter;
import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.annotation.handler.SaCheckAccessTokenHandler;
import cn.dev33.satoken.oauth2.annotation.handler.SaCheckClientIdSecretHandler;
import cn.dev33.satoken.oauth2.annotation.handler.SaCheckClientTokenHandler;
import cn.dev33.satoken.oauth2.config.SaOAuth2ServerConfig;
import cn.dev33.satoken.oauth2.data.loader.SaOAuth2DataLoader;
import cn.dev33.satoken.oauth2.scope.handler.SaOAuth2ScopeHandlerInterface;
import cn.dev33.satoken.oauth2.strategy.SaOAuth2Strategy;
import cn.dev33.satoken.oauth2.template.SaOAuth2Template;
import cn.dev33.satoken.same.SaSameTemplate;
import cn.dev33.satoken.sign.SaSignTemplate;
import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import cn.dev33.satoken.sso.config.SaSsoServerConfig;
import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;
import cn.dev33.satoken.sso.template.SaSsoClientTemplate;
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import cn.dev33.satoken.temp.SaTempInterface;
import cn.hserver.core.interfaces.PluginAdapter;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.ref.PackageScanner;
import cn.hserver.plugin.satoken.config.SaTokenContextForHServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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
        if (saSignTemplate != null) {
            SaManager.setSaSignTemplate(saSignTemplate);
        }

        SaTokenSecondContextCreator saTokenSecondContextCreator = IocUtil.getSupperBean(SaTokenSecondContextCreator.class);
        if (saTokenSecondContextCreator != null) {
            SaManager.setSaTokenSecondContext(saTokenSecondContextCreator.create());
        }

        List<SaTokenListener> saTokenListenerList = IocUtil.getSupperBeanList(SaTokenListener.class);
        if (saTokenListenerList != null) {
            for (SaTokenListener saTokenListener : saTokenListenerList) {
                SaTokenEventCenter.registerListener(saTokenListener);
            }
        }

        List<SaAnnotationHandlerInterface> saAnnotationHandlerInterfaces = IocUtil.getSupperBeanList(SaAnnotationHandlerInterface.class);
        if (saAnnotationHandlerInterfaces != null) {
            for (SaAnnotationHandlerInterface saAnnotationHandlerInterface : saAnnotationHandlerInterfaces) {
                SaAnnotationStrategy.instance.registerAnnotationHandler(saAnnotationHandlerInterface);
            }
        }


        SaTempInterface saTempInterface = IocUtil.getSupperBean(SaTempInterface.class);
        if (saTempInterface != null) {
            SaManager.setSaTemp(saTempInterface);
        }
        SaJsonTemplate saJsonTemplate = IocUtil.getSupperBean(SaJsonTemplate.class);
        if (saJsonTemplate != null) {
            SaManager.setSaJsonTemplate(saJsonTemplate);
        }

        SaSameTemplate saSameTemplate = IocUtil.getSupperBean(SaSameTemplate.class);
        if (saSameTemplate != null) {
            SaManager.setSaSameTemplate(saSameTemplate);
        }

        SaTokenConfig saTokenConfig = IocUtil.getSupperBean(SaTokenConfig.class);
        if (saTokenConfig != null) {
            SaManager.setConfig(saTokenConfig);
        }


        SaHttpBasicTemplate saHttpBasicTemplate = IocUtil.getSupperBean(SaHttpBasicTemplate.class);
        if (saHttpBasicTemplate != null) {
            SaHttpBasicUtil.saHttpBasicTemplate = saHttpBasicTemplate;
        }

        //OAuth
        SaOAuth2Template saOAuth2Template = IocUtil.getSupperBean(SaOAuth2Template.class);
        if (saOAuth2Template != null) {
            SaOAuth2Manager.setTemplate(saOAuth2Template);
        }
        SaOAuth2ServerConfig saOAuth2ServerConfig = IocUtil.getSupperBean(SaOAuth2ServerConfig.class);
        if (saOAuth2ServerConfig != null) {
            SaOAuth2Manager.setServerConfig(saOAuth2ServerConfig);
        }

        SaOAuth2DataLoader saOAuth2DataLoader = IocUtil.getSupperBean(SaOAuth2DataLoader.class);
        if (saOAuth2DataLoader != null) {
            SaOAuth2Manager.setDataLoader(saOAuth2DataLoader);
        }

        List<SaOAuth2ScopeHandlerInterface> saOAuth2ScopeHandlerInterfaces = IocUtil.getSupperBeanList(SaOAuth2ScopeHandlerInterface.class);
        if (saOAuth2ScopeHandlerInterfaces != null) {
            for (SaOAuth2ScopeHandlerInterface saOAuth2ScopeHandlerInterface : saOAuth2ScopeHandlerInterfaces) {
                SaOAuth2Strategy.instance.registerScopeHandler(saOAuth2ScopeHandlerInterface);
            }
        }

        //註冊Oauth注解
        SaAnnotationStrategy.instance.registerAnnotationHandler(new SaCheckAccessTokenHandler());
        SaAnnotationStrategy.instance.registerAnnotationHandler(new SaCheckClientIdSecretHandler());
        SaAnnotationStrategy.instance.registerAnnotationHandler(new SaCheckClientTokenHandler());


        //SSO
        SaSsoServerTemplate saSsoServerTemplate = IocUtil.getSupperBean(SaSsoServerTemplate.class);
        if (saSsoServerTemplate != null) {
            SaSsoServerProcessor.instance.ssoServerTemplate = saSsoServerTemplate;
        }
        SaSsoClientTemplate saSsoClientTemplate = IocUtil.getSupperBean(SaSsoClientTemplate.class);
        if (saSsoClientTemplate != null) {
            SaSsoClientProcessor.instance.ssoClientTemplate = saSsoClientTemplate;
        }
        SaSsoServerConfig saSsoServerConfig = IocUtil.getSupperBean(SaSsoServerConfig.class);
        if (saSsoServerConfig != null) {
            SaSsoManager.setServerConfig(saSsoServerConfig);
        }
        SaSsoClientConfig saSsoClientConfig = IocUtil.getSupperBean(SaSsoClientConfig.class);
        if (saSsoClientConfig != null) {
            SaSsoManager.setClientConfig(saSsoClientConfig);
        }
        log.info("Sa-Token插件启动成功");
    }
}
