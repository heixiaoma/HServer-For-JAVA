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

    /**
     * app 启动调用
     */
    void startApp();

    /**
     * 开始初始化
     */
    void startIocInit();


    /**
     * 初始化beanList情况，有的bean有多个实现
     */
    Set<Class<?>> iocInitBeanList();


    /**
     * ioc初始化开始
     */
    void iocInit(PackageScanner packageScanner);

    /**
     * ioc初始化完成
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
