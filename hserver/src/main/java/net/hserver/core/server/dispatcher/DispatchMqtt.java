package net.hserver.core.server.dispatcher;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import net.hserver.core.interfaces.ProtocolDispatcherAdapter;
import net.hserver.core.ioc.annotation.Bean;
import net.hserver.core.ioc.annotation.Order;
import net.hserver.core.server.codec.WebSocketMqttCodec;
import net.hserver.core.server.context.ConstConfig;
import net.hserver.core.server.handlers.MqttHeartBeatBrokerHandler;

/**
 * @author hxm
 */
@Order(4)
@Bean
public class DispatchMqtt implements ProtocolDispatcherAdapter {

    @Override
    public boolean dispatcher(ChannelHandlerContext ctx, ChannelPipeline pipeline, byte[] headers) {
        if (isMqtt(headers[0], headers[1])) {
            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(ConstConfig.HTTP_CONTENT_SIZE));
            pipeline.addLast(new HttpContentCompressor());
            pipeline.addLast(new WebSocketServerProtocolHandler("/mqtt", "mqtt,mqttv3.1,mqttv3.1.1", true, 65536));
            pipeline.addLast(new WebSocketMqttCodec());
            pipeline.addLast(new MqttDecoder());
            pipeline.addLast(MqttEncoder.INSTANCE);
            pipeline.addLast(ConstConfig.BUSINESS_EVENT, MqttHeartBeatBrokerHandler.INSTANCE);
            return true;
        }
        return false;
    }

    private boolean isMqtt(int magic1, int magic2) {
        return magic1 == 16 && magic2 == 44;
    }
}
