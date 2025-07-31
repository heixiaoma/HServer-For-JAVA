package cn.hserver.core.plugin;

import cn.hserver.core.ioc.bean.BeanDefinition;
import cn.hserver.core.plugin.bean.PluginInfo;
import cn.hserver.core.plugin.handler.PluginAdapter;

import java.util.HashSet;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * 插件管理器
 *
 * @author hxm
 */
public class PluginManager extends PluginAdapter {
    private final Set<String> plugPackages = new HashSet<>();
    private final Set<PluginAdapter> obj = new HashSet<>();

    private static final PluginManager PLUGS_MANAGER = new PluginManager();

    public static PluginManager getPlugin(){
        return PLUGS_MANAGER;
    }

    private PluginManager() {
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
    public void startedApp() {
        for (PluginAdapter plugAdapter : obj) {
            plugAdapter.startedApp();
        }
    }

    @Override
    public void iocStartScan() {
        for (PluginAdapter plugAdapter : obj) {
            plugAdapter.iocStartScan();
        }
    }

    @Override
    public void iocStartRegister(Map<String, BeanDefinition> beanDefinitions) {
        for (PluginAdapter plugAdapter : obj) {
            plugAdapter.iocStartRegister(beanDefinitions);
        }
    }

    @Override
    public void iocStartPopulate(Map<String, BeanDefinition> beanDefinitions) {
        for (PluginAdapter plugAdapter : obj) {
            plugAdapter.iocStartPopulate(beanDefinitions);
        }
    }

    @Override
    public void startApp() {
        for (PluginAdapter plugAdapter : obj) {
            plugAdapter.startApp();
        }
    }

    public void pluginInfo() {
        for (PluginAdapter plugAdapter : obj) {
          plugAdapter.printPluginInfo();
        }
    }

    @Override
    public PluginInfo getPluginInfo() {
        return null;
    }
}
