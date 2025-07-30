package cn.hserver.core.ioc;

import cn.hserver.core.queue.annotation.QueueHandler;
import cn.hserver.core.queue.annotation.QueueListener;

@QueueListener(queueName = "test",threadSize = 1)
public class QueueTest {

    @QueueHandler
    public void queueTest(String msg) {
        System.out.println("queueTest: "+msg);
    }
}
