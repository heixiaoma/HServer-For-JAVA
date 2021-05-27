package top.hserver.core.server.dispatcher;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import top.hserver.core.interfaces.ProtocolDispatcherAdapter;
import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.ioc.annotation.Order;
import top.hserver.core.server.ServerInitializer;
import top.hserver.core.server.codec.WebSocketMqttCodec;
import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.handlers.MqttHeartBeatBrokerHandler;

/**
 * @author hxm
 */
@Order(4)
@Bean
public class DispatchMqtt implements ProtocolDispatcherAdapter {

    @Override
    public boolean dispatcher(ChannelHandlerContext ctx, ChannelPipeline pipeline, byte[] headers, ServerInitializer.ProtocolDispatcher protocolDispatcher) {
        if (isMqtt(headers[0], headers[1])) {
            System.out.println(new String(headers));
            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(ConstConfig.HTTP_CONTENT_SIZE));
            pipeline.addLast(new HttpContentCompressor());
            pipeline.addLast(new WebSocketServerProtocolHandler("/mqtt", "mqtt,mqttv3.1,mqttv3.1.1", true, 65536));
            pipeline.addLast(new WebSocketMqttCodec());
            pipeline.addLast(new MqttDecoder());
            pipeline.addLast(MqttEncoder.INSTANCE);
            pipeline.addLast(ConstConfig.BUSINESS_EVENT, MqttHeartBeatBrokerHandler.INSTANCE);
            pipeline.remove(protocolDispatcher);
            ctx.fireChannelActive();
            return true;
        }
        return false;
    }

    private boolean isMqtt(int magic1, int magic2) {
        return magic1 == 16 && magic2 == 44;
    }
}
