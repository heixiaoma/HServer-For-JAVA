## **HUM消息**

HServer UPD Message HUM消息是在局域网内服务之间的通讯，使用UPD广播方式，方便内网服务器相关的通讯


- 消息接受器
- 提供了Hum用来区分和发送消息

```java
import top.hserver.core.interfaces.HumAdapter;
import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.server.handlers.Hum;

@Bean
public class HumMsg implements HumAdapter {

    @Override
    public void message(Object o, Hum hum) {
        
        //UDP其实不分服务端和客服端，但是为了开发理解方便，加入这个
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
开发过程一定要滤清关系，避免消息到达不了
- 主动发送消息
- 支持指定端口发送，默认按自己占用端口发送
```java
 HumClient.sendMessage("666");
```
