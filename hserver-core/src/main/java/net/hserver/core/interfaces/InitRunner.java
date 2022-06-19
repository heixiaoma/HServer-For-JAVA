package net.hserver.core.interfaces;

/**
 * @author hxm
 */
public interface InitRunner {
    /**
     * 启动完成的回调
     * @param args 主函数的 args
     */
    void init(String[] args);
}
