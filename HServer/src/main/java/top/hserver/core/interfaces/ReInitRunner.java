package top.hserver.core.interfaces;

/**
 * @author hxm
 */
public interface ReInitRunner {
    /**
     * 重新初始化的回调。可以手动的关闭一些资源避免照成资源泄漏
     */
    void reInit();
}
