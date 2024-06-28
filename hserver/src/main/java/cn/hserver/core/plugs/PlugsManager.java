package cn.hserver.core.plugs;

import cn.hserver.core.ioc.ref.PackageScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.hserver.core.interfaces.PluginAdapter;
import cn.hserver.core.server.util.ExceptionUtil;

import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * 插件管理器
 *
 * @author hxm
 */
public class PlugsManager implements PluginAdapter {
    private static final Logger log = LoggerFactory.getLogger(PlugsManager.class);

    private final Set<String> plugPackages = new HashSet<>();
    private final Set<PluginAdapter> obj = new HashSet<>();

    public static final PlugsManager PLUGS_MANAGER = new PlugsManager();

    public static PlugsManager getPlugin(){
        return PLUGS_MANAGER;
    }

    private PlugsManager() {
        ServiceLoader<PluginAdapter> loadedParsers = ServiceLoader.load(PluginAdapter.class);
        for (PluginAdapter pluginAdapter : loadedParsers) {
            obj.add(pluginAdapter);
            plugPackages.add(pluginAdapter.getClass().getPackage().getName());
        }
    }

    public Set<String> getPlugPackages() {
        return plugPackages;
    }

    @Override
    public void startApp() {
        for (PluginAdapter plugAdapter : obj) {
            plugAdapter.startApp();
        }
    }

    public void addPlugins(Class... plugsClass) {
        for (Class aClass : plugsClass) {
            try {
                obj.add((PluginAdapter) aClass.newInstance());
                plugPackages.add(aClass.getPackage().getName());
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }
        }
    }

    @Override
    public void startIocInit() {
        for (PluginAdapter plugAdapter : obj) {
            plugAdapter.startIocInit();
        }
    }

    @Override
    public Set<Class<?>> iocInitBeanList() {
        Set<Class<?>> listBean=new HashSet<>();
        for (PluginAdapter plugAdapter : obj) {
            Set<Class<?>> classes = plugAdapter.iocInitBeanList();
            if (classes!=null){
                listBean.addAll(classes);
            }
        }
        return listBean;
    }

    @Override
    public void iocInit(PackageScanner packageScanner) {
        for (PluginAdapter plugAdapter : obj) {
            plugAdapter.iocInit(packageScanner);
        }
    }

    @Override
    public void iocInitEnd() {
        for (PluginAdapter plugAdapter : obj) {
            plugAdapter.iocInitEnd();
        }
    }

    @Override
    public void startInjection() {
        for (PluginAdapter plugAdapter : obj) {
            plugAdapter.startInjection();
        }
    }

    @Override
    public void injectionEnd() {
        for (PluginAdapter plugAdapter : obj) {
            plugAdapter.injectionEnd();
        }
    }
}
