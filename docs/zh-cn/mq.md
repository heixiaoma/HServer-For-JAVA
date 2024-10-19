
## 消息队列的使用

队列核心技术使用的是disruptor+leveldb 无序队列和延时队列

生产者

```java
  @GET("/event")
    public JsonResult event() {
    	//队列名字，方法参数
        //持久方式，会先缓存文件，在消费
        HServerQueue.sendQueue("Queue", "666");
        return JsonResult.ok();
    }


    @GET("/eventInfo")
    public JsonResult eventInfo() {
        //剩余队列数
        // long size;
        
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
}
```

我们在使用该注解时，会考虑到队列的数据 重复消费还是不重复消费。我们可以指定 type类型就可以了，默认是不重复消费的

```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface QueueHandler {
    //消费者数量
    int size() default 1;

    //配置文件读取消息数量
    String sizePropValue() default "";
}
```

消费方法定义，消费者数量默认是1，我们可以定义多个，通过size参数处理

一个类中只能有一个消费者，也就是一个@QueueHandler 注解



```java
import lombok.extern.slf4j.Slf4j;
import test1.service.HelloService;
import cn.hserver.core.ioc.annotation.Autowired;
import cn.hserver.core.ioc.annotation.queue.QueueHandler;
import cn.hserver.core.ioc.annotation.queue.QueueListener;

import java.util.concurrent.atomic.LongAdder;

@Slf4j
@QueueListener(queueName = "Queue")
public class EventTest {

    @Autowired
    private HelloService helloService;

    LongAdder atomicLong = new LongAdder();

    @QueueHandler( size = 2)
    public void aa(String name) {
        atomicLong.increment();
        System.out.println(atomicLong + "---------" + Thread.currentThread().getName());
    }

    //一个类中只能有一个消费者，也就是一个@QueueHandler 注解
    
    //优先配置文件，如果配置文件不存在apm.size 则使用size 8 
    @QueueHandler(sizePropValue = "apm.size",size = 8)
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
