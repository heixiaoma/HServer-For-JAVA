package test1.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test1.service.HelloService;
import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.queue.QueueHandler;
import top.hserver.core.ioc.annotation.queue.QueueListener;

import java.util.concurrent.atomic.LongAdder;

@QueueListener(queueName = "Queue")
public class EventTest {

    private static final Logger log = LoggerFactory.getLogger(EventTest.class);

    @Autowired
    private HelloService helloService;

    LongAdder atomicLong = new LongAdder();

    @QueueHandler(level = 1, size = 2)
    public void aa(String name) {
        atomicLong.increment();
        System.out.println(atomicLong + "---------" + Thread.currentThread().getName());
        throw new NullPointerException("try test");
    }


    @QueueHandler(level = 2, size = 2)
    public void cc(String name) {
        atomicLong.increment();
        System.out.println(atomicLong + "---------" + Thread.currentThread().getName());
        throw new NullPointerException("try test");
    }
}
