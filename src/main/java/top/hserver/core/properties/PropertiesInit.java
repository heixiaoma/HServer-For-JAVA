package top.hserver.core.properties;

import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.util.PropUtil;


/**
 * @author hxm
 */
public class PropertiesInit {

    public static void init() {
        PropUtil instance = PropUtil.getInstance();
        Integer taskPool = instance.getInt("taskPool");
        if (taskPool != null) {
            ConstConfig.taskPool = taskPool;
        }
        Integer queuePool = instance.getInt("queuePool");
        if (taskPool != null) {
            ConstConfig.queuePool = queuePool;
        }
        Integer bossPool = instance.getInt("bossPool");
        if (bossPool != null) {
            ConstConfig.bossPool = bossPool;
        }
        Integer workerPool = instance.getInt("workerPool");
        if (workerPool != null) {
            ConstConfig.workerPool = workerPool;
        }
        Boolean epoll = Boolean.valueOf(instance.get("epoll"));
        ConstConfig.EPOLL = epoll;
    }

}
