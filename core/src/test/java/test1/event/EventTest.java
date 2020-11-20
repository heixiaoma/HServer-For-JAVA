package test1.event;

import lombok.extern.slf4j.Slf4j;
import test1.service.HelloService;
import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.queue.QueueHandler;
import top.hserver.core.ioc.annotation.queue.QueueListener;

import java.util.concurrent.atomic.LongAdder;

@Slf4j
@QueueListener(queueName = "Queue")
public class EventTest {

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
