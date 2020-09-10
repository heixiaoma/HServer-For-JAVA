package top.hserver.core.interfaces;


/**
 * 插件适配器
 *
 * @author hxm
 */
public interface PlugAdapter {

    /**
     * 开始初始化
     */
    void startIocInit();

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
