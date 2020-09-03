package test1.event;

import lombok.extern.slf4j.Slf4j;
import test1.service.HelloService;
import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.queue.QueueHandlerType;
import top.hserver.core.ioc.annotation.queue.QueueHanler;
import top.hserver.core.ioc.annotation.queue.QueueListener;

import java.util.concurrent.atomic.LongAdder;

@Slf4j
@QueueListener(queueName = "Queue", type = QueueHandlerType.NO_REPEAT_CONSUMPTION)
public class EventTest {

    @Autowired
    private HelloService helloService;

    LongAdder atomicLong = new LongAdder();

    @QueueHanler(level = 1, size = 20)
    public void aa(String name) {
        atomicLong.increment();
        if (atomicLong.intValue() % 100000000 == 0) {
            log.info(atomicLong.intValue() + "");
        }

    }

}
