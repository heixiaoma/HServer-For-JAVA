package cn.hserver.plugin.satoken;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.handler.SaAnnotationHandlerInterface;
import cn.dev33.satoken.apikey.SaApiKeyManager;
import cn.dev33.satoken.apikey.config.SaApiKeyConfig;
import cn.dev33.satoken.apikey.loader.SaApiKeyDataLoader;
import cn.dev33.satoken.apikey.template.SaApiKeyTemplate;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaTokenContext;
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
import cn.dev33.satoken.sign.SaSignManager;
import cn.dev33.satoken.sign.template.SaSignTemplate;
import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import cn.dev33.satoken.sso.config.SaSsoServerConfig;
import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;
import cn.dev33.satoken.sso.template.SaSsoClientTemplate;
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import cn.dev33.satoken.temp.SaTempTemplate;
import cn.hserver.core.context.IocApplicationContext;
import cn.hserver.core.plugin.bean.PluginInfo;
import cn.hserver.core.plugin.handler.PluginAdapter;
import cn.hserver.plugin.satoken.config.SaTokenContextForHServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class SaTokenPlugin extends PluginAdapter {
    private static final Logger log = LoggerFactory.getLogger(SaTokenPlugin.class);



    @Override
    public void startApp() {

    }

    @Override
    public PluginInfo getPluginInfo() {
        return new PluginInfo.Builder()
                .name("sa-token")
                .description("Sa-Token 是一个轻量级 Java 权限认证框架，主要解决：登录认证、权限认证、单点登录、OAuth2.0、分布式Session会话、微服务网关鉴权 等一系列权限相关问题。")
                .build();
    }


    @Override
    public void iocStartPopulate() {

        SaTokenContext saTokenContext = new SaTokenContextForHServer();
        SaManager.setSaTokenContext(saTokenContext);
        StpInterface stpInterface = IocApplicationContext.getBeansOfTypeOne(StpInterface.class);
        if (stpInterface != null) {
            SaManager.setStpInterface(stpInterface);
        }
        SaTokenDao saTokenDao = IocApplicationContext.getBeansOfTypeOne(SaTokenDao.class);
        if (saTokenDao != null) {
            SaManager.setSaTokenDao(saTokenDao);
        }

        SaSignTemplate saSignTemplate = IocApplicationContext.getBeansOfTypeOne(SaSignTemplate.class);
        if (saSignTemplate != null) {
            SaSignManager.setSaSignTemplate(saSignTemplate);
        }


        List<SaTokenListener> saTokenListenerList = IocApplicationContext.getBeansOfTypeSorted(SaTokenListener.class);
        if (saTokenListenerList != null) {
            for (SaTokenListener saTokenListener : saTokenListenerList) {
                SaTokenEventCenter.registerListener(saTokenListener);
            }
        }

        List<SaAnnotationHandlerInterface> saAnnotationHandlerInterfaces = IocApplicationContext.getBeansOfTypeSorted(SaAnnotationHandlerInterface.class);
        if (saAnnotationHandlerInterfaces != null) {
            for (SaAnnotationHandlerInterface<?> saAnnotationHandlerInterface : saAnnotationHandlerInterfaces) {
                SaAnnotationStrategy.instance.registerAnnotationHandler(saAnnotationHandlerInterface);
            }
        }
        SaApiKeyTemplate apiKeyTemplate=IocApplicationContext.getBeansOfTypeOne(SaApiKeyTemplate.class);
        if (apiKeyTemplate != null) {
            SaApiKeyManager.setSaApiKeyTemplate(apiKeyTemplate);
        }

        SaApiKeyConfig saApiKeyConfig=IocApplicationContext.getBeansOfTypeOne(SaApiKeyConfig.class);
        if (saApiKeyConfig != null) {
            SaApiKeyManager.setConfig(saApiKeyConfig);
        }

        SaApiKeyDataLoader apiKeyDataLoader=IocApplicationContext.getBeansOfTypeOne(SaApiKeyDataLoader .class);
        if (apiKeyDataLoader != null) {
            SaApiKeyManager.setSaApiKeyDataLoader(apiKeyDataLoader);
        }

        SaTempTemplate saTempInterface = IocApplicationContext.getBeansOfTypeOne(SaTempTemplate.class);
        if (saTempInterface != null) {
            SaManager.setSaTempTemplate(saTempInterface);
        }
        SaJsonTemplate saJsonTemplate = IocApplicationContext.getBeansOfTypeOne(SaJsonTemplate.class);
        if (saJsonTemplate != null) {
            SaManager.setSaJsonTemplate(saJsonTemplate);
        }

        SaSameTemplate saSameTemplate = IocApplicationContext.getBeansOfTypeOne(SaSameTemplate.class);
        if (saSameTemplate != null) {
            SaManager.setSaSameTemplate(saSameTemplate);
        }

        SaTokenConfig saTokenConfig = IocApplicationContext.getBeansOfTypeOne(SaTokenConfig.class);
        if (saTokenConfig != null) {
            SaManager.setConfig(saTokenConfig);
        }


        SaHttpBasicTemplate saHttpBasicTemplate = IocApplicationContext.getBeansOfTypeOne(SaHttpBasicTemplate.class);
        if (saHttpBasicTemplate != null) {
            SaHttpBasicUtil.saHttpBasicTemplate = saHttpBasicTemplate;
        }

        //OAuth
        SaOAuth2Template saOAuth2Template = IocApplicationContext.getBeansOfTypeOne(SaOAuth2Template.class);
        if (saOAuth2Template != null) {
            SaOAuth2Manager.setTemplate(saOAuth2Template);
        }
        SaOAuth2ServerConfig saOAuth2ServerConfig = IocApplicationContext.getBeansOfTypeOne(SaOAuth2ServerConfig.class);
        if (saOAuth2ServerConfig != null) {
            SaOAuth2Manager.setServerConfig(saOAuth2ServerConfig);
        }

        SaOAuth2DataLoader saOAuth2DataLoader = IocApplicationContext.getBeansOfTypeOne(SaOAuth2DataLoader.class);
        if (saOAuth2DataLoader != null) {
            SaOAuth2Manager.setDataLoader(saOAuth2DataLoader);
        }

        List<SaOAuth2ScopeHandlerInterface> saOAuth2ScopeHandlerInterfaces = IocApplicationContext.getBeansOfTypeSorted(SaOAuth2ScopeHandlerInterface.class);
        if (saOAuth2ScopeHandlerInterfaces != null) {
            for (SaOAuth2ScopeHandlerInterface saOAuth2ScopeHandlerInterface : saOAuth2ScopeHandlerInterfaces) {
                SaOAuth2Strategy.instance.registerScopeHandler(saOAuth2ScopeHandlerInterface);
            }
        }

        //注册Oauth注解
        SaAnnotationStrategy.instance.registerAnnotationHandler(new SaCheckAccessTokenHandler());
        SaAnnotationStrategy.instance.registerAnnotationHandler(new SaCheckClientIdSecretHandler());
        SaAnnotationStrategy.instance.registerAnnotationHandler(new SaCheckClientTokenHandler());


        //SSO
        SaSsoServerTemplate saSsoServerTemplate = IocApplicationContext.getBeansOfTypeOne(SaSsoServerTemplate.class);
        if (saSsoServerTemplate != null) {
            SaSsoServerProcessor.instance.ssoServerTemplate = saSsoServerTemplate;
        }
        SaSsoClientTemplate saSsoClientTemplate = IocApplicationContext.getBeansOfTypeOne(SaSsoClientTemplate.class);
        if (saSsoClientTemplate != null) {
            SaSsoClientProcessor.instance.ssoClientTemplate = saSsoClientTemplate;
        }
        SaSsoServerConfig saSsoServerConfig = IocApplicationContext.getBeansOfTypeOne(SaSsoServerConfig.class);
        if (saSsoServerConfig != null) {
            SaSsoManager.setServerConfig(saSsoServerConfig);
        }
        SaSsoClientConfig saSsoClientConfig = IocApplicationContext.getBeansOfTypeOne(SaSsoClientConfig.class);
        if (saSsoClientConfig != null) {
            SaSsoManager.setClientConfig(saSsoClientConfig);
        }
        log.info("Sa-Token插件启动成功");
    }
}
