package top.hserver.core.interfaces;

/**
 * @author hxm
 */
public interface TaskJob {

    /**
     * 定时任务
     * @param args
     */
    void exec(Object... args);

}
