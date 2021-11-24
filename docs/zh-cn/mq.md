
## 消息队列的使用

队列核心技术使用的是disruptor

生产者

```java
  @GET("/event")
    public JsonResult event() {
    	//队列名字，方法参数
        //默认队列1024，超过限制就阻塞
        HServerQueue.sendQueue("Queue", "666");
        
        //持久方式，会先缓存文件，在消费
        HServerQueue.sendPersistQueue("Queue", "666");
        return JsonResult.ok();
    }


    @GET("/eventInfo")
    public JsonResult eventInfo() {
        QueueInfo queueInfo = HServerQueue.queueInfo("Queue");
        return JsonResult.ok().put("data", queueInfo);
    }

```

消费者定义

```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface QueueListener {
    /**
     * 队列名
     *
     * @return
     */
    String queueName() default "";

    /**
     * 消费者类型
     *
     * @return
     */
    QueueHandlerType type() default QueueHandlerType.NO_REPEAT;
}
```

我们在使用该注解时，会考虑到队列的数据 重复消费还是不重复消费。我们可以指定 type类型就可以了，默认是不重复消费的

```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface QueueHandler {
    //消费者优先级 级别重小到大排序，小的有限，同一样的就并行操作
    int level() default 1;

    //消费者数量
    int size() default 1;

}
```

消费方法定义，消费者数量默认是1，我们可以定义多个，通过size参数处理

level级别,当里面出现两个 我们指定级别实现 顺序消费.

我们可以通过消费类型和消费级别可以自由组合，多种方案



```java
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
    }


    @QueueHandler(level = 2, size = 2)
    public void cc(String name) {
        atomicLong.increment();
        System.out.println(atomicLong + "---------" + Thread.currentThread().getName());
    }
}

```

动态Queue使用方法
```java

@QueueListener
public class QueueTest2 {

    @Autowired
    private TestService testService;

    @QueueHandler
    public void aVoid(String a) {
        String name = testService.getName();
        System.out.println(name + a);
    }
}


//动态queue中 QueueListener 不需要指定queueName
HServerQueue.addQueueListener("A", QueueTest2.class);
for (int i = 0; i < 10; i++) {
    HServerQueue.sendQueue("A", i + "");
}
HServerQueue.removeQueue("A");

```