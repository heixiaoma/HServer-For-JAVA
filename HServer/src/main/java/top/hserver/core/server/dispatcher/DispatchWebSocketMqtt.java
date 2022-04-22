package top.hserver.core.server.dispatcher;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import top.hserver.core.interfaces.ProtocolDispatcherAdapter;
import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.ioc.annotation.Order;
import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.handlers.MqttHeartBeatBrokerHandler;

/**
 * @author hxm
 */
@Order(2)
@Bean
public class DispatchWebSocketMqtt implements ProtocolDispatcherAdapter {

    @Override
    public boolean dispatcher(ChannelHandlerContext ctx, ChannelPipeline pipeline, byte[] headers) {
        if (headers[0] == 'G' && headers[1] == 'E' && new String(headers).indexOf("Sec-WebSocket-Protocol: mqtt") > 0) {
            pipeline.addLast(MqttEncoder.INSTANCE);
            pipeline.addLast(new MqttDecoder());
            pipeline.addLast(ConstConfig.BUSINESS_EVENT, MqttHeartBeatBrokerHandler.INSTANCE);
            return true;
        }
        return false;
    }
}
