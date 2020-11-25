package top.hserver.core.queue;

import lombok.Data;
import top.hserver.core.ioc.annotation.queue.QueueHandlerType;

import java.util.ArrayList;
import java.util.List;

/**
 * 队列信息
 *
 * @author hxm
 */
@Data
public class QueueHandleInfo {

    private QueueFactory queueFactory;

    private String queueName;

    private int bufferSize;

    private QueueHandlerType queueHandlerType;

    private List<QueueHandleMethod> queueHandleMethods = new ArrayList<>();

    public void add(QueueHandleMethod eventHandleMethod) {
        this.queueHandleMethods.add(eventHandleMethod);
    }

}
