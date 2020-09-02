package test1.event;

import lombok.extern.slf4j.Slf4j;
import test1.service.HelloService;
import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.event.Event;
import top.hserver.core.ioc.annotation.event.EventHandler;
import top.hserver.core.ioc.annotation.event.EventHandlerType;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
@EventHandler(queueName = "Queue",type = EventHandlerType.NO_REPEAT_CONSUMPTION,bufferSize = 65536)
public class EventTest {

    @Autowired
    private HelloService helloService;

    LongAdder atomicLong=new LongAdder();

    @Event(level = 1,size = 20)
    public void aa(String name) {
        atomicLong.increment();
        if (atomicLong.intValue()%100000000==0){
            log.info(atomicLong.intValue()+"");
        }

    }

}
