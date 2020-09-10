package top.hserver.core;

import top.hserver.core.interfaces.PlugAdapter;

import java.util.HashSet;
import java.util.Set;

/**
 * 插件管理器
 */
public class PlugsManager implements PlugAdapter {

    private Set<String> plugPackages = new HashSet<>();
    private Set<PlugAdapter> obj = new HashSet<>();


    public Set<String> getPlugPackages() {
        return plugPackages;
    }

    public void addPlugs(Class... plugsClass) {
        for (Class aClass : plugsClass) {
            try {
                obj.add((PlugAdapter) aClass.newInstance());
                plugPackages.add(aClass.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void startIocInit() {
        for (PlugAdapter plugAdapter : obj) {
            plugAdapter.startIocInit();
        }
    }

    @Override
    public void IocInitEnd() {
        for (PlugAdapter plugAdapter : obj) {
            plugAdapter.IocInitEnd();
        }
    }

    @Override
    public void startInjection() {
        for (PlugAdapter plugAdapter : obj) {
            plugAdapter.startInjection();
        }
    }

    @Override
    public void injectionEnd() {
        for (PlugAdapter plugAdapter : obj) {
            plugAdapter.injectionEnd();
        }
    }
}
