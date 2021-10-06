
## **自定义协议**

案例：https://gitee.com/HServer/hsvevr-for-custom-protocol

HServer实现的是同端口多协议处理方式，如果有需要还可以构建其他协议。
构建协议特点，需要包含包头用于区分协议，选择对应的解码器处理。
HServer默认集成 websocket http rpc mqtt websocketmqtt协议，
如果你还有更多的需求，可以在加协议。或者重写HServer提供的协议。
headers字节，最多提取头512个字节，所以定义头一定不要过长，不要过线.

比如重写Http协议；
```java
@Order(3)
@Bean
public class HttpProtocol extends DispatchHttp {
    @Override
    public boolean dispatcher(ChannelHandlerContext ctx, ChannelPipeline pipeline, byte[] headers, ServerInitializer.ProtocolDispatcher protocolDispatcher) {
        return super.dispatcher(ctx, pipeline, headers, protocolDispatcher);
    }
}
```
这里需要注意一点的是@Order(3)注解，由于WebSocketMqtt是基于Http协议的，所以先判断的WebSocketMqtt协议，WebSocketMqtt为@Order(2)，返回true者不向下执行。
Order值越越小，优先级越高。

如何自定义协议呢？比如实现一个MQTT或者其他自定义的协议
```java

@Order(2)
@Bean
public class DispatchWebSocketMqtt implements ProtocolDispatcherAdapter {
    @Override
    public boolean dispatcher(ChannelHandlerContext ctx, ChannelPipeline pipeline, byte[] headers, ServerInitializer.ProtocolDispatcher protocolDispatcher) {
        if (headers[0] == 'G' && headers[1] == 'E' && new String(headers).indexOf("Sec-WebSocket-Protocol: mqtt") > 0) {
            pipeline.addLast(MqttEncoder.INSTANCE);
            pipeline.addLast(new MqttDecoder());
            pipeline.addLast(ConstConfig.BUSINESS_EVENT, MqttHeartBeatBrokerHandler.INSTANCE);
            return true;
        }
        return false;
    }
}
```
这里需要注意他的头包含GET 但是http协议也是包含GET，所以这个协议一定要优先于HTTP检查。自己在定义协议的时候，尽量采用特殊字符作为消息头，用来区分协议。
剩下的操作就和Netty原生开发方案类似了，定义编码解码器，最后到自己的Handler处理器里。
非常重要一点，一定不要在这里面有阻塞操作，不然会卡的批爆。切记切记。