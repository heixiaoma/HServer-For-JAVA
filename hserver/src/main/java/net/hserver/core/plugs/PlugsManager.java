package net.hserver.core.plugs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hserver.core.interfaces.PluginAdapter;
import net.hserver.core.server.util.ExceptionUtil;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * 插件管理器
 *
 * @author hxm
 */
public class PlugsManager implements PluginAdapter {
    private static final Logger log = LoggerFactory.getLogger(PlugsManager.class);

    private Set<String> plugPackages = new HashSet<>();
    private Set<PluginAdapter> obj = new HashSet<>();

    public PlugsManager() {
        ServiceLoader<PluginAdapter> loadedParsers = ServiceLoader.load(PluginAdapter.class);
        for (PluginAdapter pluginAdapter : loadedParsers) {
            obj.add(pluginAdapter);
            plugPackages.add(pluginAdapter.getClass().getPackage().getName());
        }
    }

    public Set<String> getPlugPackages() {
        return plugPackages;
    }

    public void addPlugins(Class... plugsClass) {
        for (Class aClass : plugsClass) {
            try {
                obj.add((PluginAdapter) aClass.newInstance());
                plugPackages.add(aClass.getPackage().getName());
            } catch (Exception e) {
                log.error(ExceptionUtil.getMessage(e));
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
