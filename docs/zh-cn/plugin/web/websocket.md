##	WebSocket

需要被@WebSocket标注同时给一个连接地址，最后实现WebSocketHandler接口，
Ws类定义了简单的发送方法，如果有其他的业务操作，可以获取ChannelHandlerContext，进行操作

```java


@WebSocket("/ws")
public class WebSocketTest implements WebSocketHandler {

    @Autowired
    private TestService testService;

    @Override
    public void onConnect(Ws ws) {
        System.out.println("连接成功,分配的UID：" + ws.getUid());
    }

    @Override
    public void onMessage(Ws ws) {
        ws.send("666" + testService.testa() + ws.getUid());
        System.out.println("收到的消息,"+ws.getMessage()+",UID：" + ws.getUid());
    }

    @Override
    public void disConnect(Ws ws) {
        System.out.println("断开连接,UID:" + ws.getUid());
    }
}
```

ws类提供了Netty原始的HttpRequest对象，你可以自由处理，同时提供了 query 函数，帮助你快速查找到websocket URL的参数

