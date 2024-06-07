package cn.hserver.core.interfaces;


import cn.hserver.core.ioc.ref.PackageScanner;

import java.util.List;
import java.util.Set;

/**
 * 插件适配器
 *
 * @author hxm
 */
public interface PluginAdapter {

    void startApp();

    /**
     * 开始初始化
     */
    void startIocInit();


    Set<Class<?>> iocInitBeanList();


    void iocInit(PackageScanner packageScanner);

    /**
     * 初始化完成
     */
    void iocInitEnd();

    /**
     * 开始注入
     */
    void startInjection();

    /**
     * 注入完成
     */
    void injectionEnd();

}
