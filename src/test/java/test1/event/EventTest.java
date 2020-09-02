package test1.event;

import lombok.extern.slf4j.Slf4j;
import test1.service.HelloService;
import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.event.Event;
import top.hserver.core.ioc.annotation.event.EventHandler;
import top.hserver.core.ioc.annotation.event.EventHandlerType;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@EventHandler(queueName = "Queue",type = EventHandlerType.NO_REPEAT_CONSUMPTION)
public class EventTest {

    @Autowired
    private HelloService helloService;

    AtomicLong atomicLong=new AtomicLong(0);

    @Event(level = 1,size = 2)
    public void aa(String name) {

        long l = atomicLong.incrementAndGet();
        if (l%1000000==0){
            log.info(l+"");
        }

    }

}
