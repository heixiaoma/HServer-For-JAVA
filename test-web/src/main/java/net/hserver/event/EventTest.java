package net.hserver.event;

import net.hserver.service.HelloService;
import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.queue.QueueHandler;
import top.hserver.core.ioc.annotation.queue.QueueListener;

import java.util.concurrent.atomic.LongAdder;

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
