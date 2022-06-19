## **Mqtt**

请继承MqttAdapter类 并用@Bean 标记
其他操作可以重写父类的一些方法

```java

@Bean
public class Mqtt extends MqttAdapter {

    @Override
    public void message(MqttMessageType mqttMessageType, MqttMessage mqttMessage, ChannelHandlerContext channelHandlerContext) {
        System.out.println(mqttMessageType);
    }
}


```
