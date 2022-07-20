package cn.hserver.core.interfaces;


import cn.hserver.core.ioc.ref.PackageScanner;

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


    boolean iocInitBean(Class classz);


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
