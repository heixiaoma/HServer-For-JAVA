## **HUM消息**

HServer UPD Message HUM消息是在局域网内服务之间的通讯，实现数据共享，使用UPD广播方式，方便内网服务器相关的通讯，注意此协议是不可靠的，也就是可能会存在数据发生不过去的情况，或者接受不到数据，因此在编码过程中，需要注意这点。



- 消息接受器
- 提供了Hum用来区分和发送消息

```java
import cn.hserver.core.interfaces.HumAdapter;
import cn.hserver.core.ioc.annotation.Bean;
import cn.hserver.core.server.handlers.Hum;

@Bean
public class HumMsg implements HumAdapter {

    @Override
    public void message(Object o, Hum hum) {
        
        //UDP其实不分服务端和客服端，但是为了开发理解方便，加入这个
        // hum是点对点推送
        System.out.println(hum.getType()+"-->Hum消息："+o);
        if (hum.getType()== Hum.Type.CLIENT){
            hum.sendMessage(System.currentTimeMillis()+"Server");
        }
        else {
            hum.sendMessage(System.currentTimeMillis()+"Client");
        }
    }
}
```

- 主动发送消息
- 默认9527端口，可以在配置文件中自定义端口: 
```properties
humPort=9527
#开启hum 默认开启
humOpen=true
```
```java
//广播推送
 HumClient.sendMessage("666");
```
