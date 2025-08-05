package cn.hserver.mvc;

import cn.hserver.core.config.ConfigData;
import cn.hserver.core.context.handler.AnnotationHandler;
import cn.hserver.core.ioc.bean.BeanDefinition;
import cn.hserver.core.plugin.bean.PluginInfo;
import cn.hserver.core.plugin.handler.PluginAdapter;
import cn.hserver.mvc.annotation.Controller;
import cn.hserver.mvc.annotation.WebSocket;
import cn.hserver.mvc.constants.WebConstConfig;
import cn.hserver.mvc.server.WebServer;
import cn.hserver.mvc.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public class MvcPlugin extends PluginAdapter {
    private static final Logger log = LoggerFactory.getLogger(MvcPlugin.class);
    private final List<Class<?>> controllers = new ArrayList<>();
    public static WebServer webServer;
    @Override
    public PluginInfo getPluginInfo() {
        return new PluginInfo.Builder().name("MVC").description( "灵活高性能的WEB框架").build();
    }

    /**
     * 注册控制器
     */
    @Override
    public void startApp() {
        AnnotationHandler.addHandler(new AnnotationHandler() {
            @Override
            public void handle(Class<?> clazz, Map<String, BeanDefinition> beanDefinitions) {
                if(clazz.isAnnotationPresent(Controller.class)){
                    defaultHandler(clazz, beanDefinitions);
                }
                if(clazz.isAnnotationPresent(WebSocket.class)){
                    defaultHandler(clazz, beanDefinitions);
                }
            }
        });

        //初始化环境配置依赖
        ConfigData instance = ConfigData.getInstance();
        WebConstConfig.PORT=instance.getInteger("web.port", 8888);
        WebConstConfig.SESSION_TIME_OUT=instance.getInteger("web.session.timeout", 7200);
        Boolean session = instance.getBoolean("web.session.enable", false);
        if (session) {
            WebConstConfig.SESSION_MANAGER=new SessionManager();
        }
    }

    @Override
    public void iocStartScan(Class<?> clazz) {
        if(clazz.isAnnotationPresent(Controller.class)){
            controllers.add(clazz);
        }
    }

    @Override
    public void iocStartPopulate() {
        //可以开始注册路由了
        controllers.forEach(clazz -> {
            try {
                WebServer.router.registerControllerRoutes(clazz);
            } catch (InstantiationException | IllegalAccessException e) {
                log.warn(e.getMessage());
            }
        });
    }

    @Override
    public void startedApp() {
        //启动web容器服务器
        ServiceLoader<WebServer> loadedParsers = ServiceLoader.load(WebServer.class);
        for (WebServer webServer : loadedParsers) {
            webServer.start(WebConstConfig.PORT);
            MvcPlugin.webServer = webServer;
            log.debug("WEB容器启动成功:{}", WebConstConfig.PORT);
            break;
        }
        if (webServer == null) {
            log.warn("WEB容器未找到,请检查是否引入了WEB容器的依赖");
        }
    }
}
