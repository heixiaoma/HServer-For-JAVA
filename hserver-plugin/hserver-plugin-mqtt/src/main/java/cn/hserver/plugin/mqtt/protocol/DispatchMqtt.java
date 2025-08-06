package cn.hserver.plugin.mqtt.protocol;

import cn.hserver.core.ioc.annotation.Value;
import cn.hserver.core.server.util.PropUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import cn.hserver.core.interfaces.ProtocolDispatcherAdapter;
import cn.hserver.core.ioc.annotation.Bean;
import cn.hserver.core.ioc.annotation.Order;
import cn.hserver.plugin.mqtt.codec.WebSocketMqttCodec;
import cn.hserver.plugin.mqtt.handlers.MqttHeartBeatBrokerHandler;

/**
 * @author hxm
 */
@Order(4)
@Bean
public class DispatchMqtt implements ProtocolDispatcherAdapter {
    private final String path= PropUtil.getInstance().get("mqtt.path","/mqtt");

    @Override
    public boolean dispatcher(ChannelHandlerContext ctx, ChannelPipeline pipeline, byte[] headers) {
        if (isMqtt(headers[0], headers[1])) {
            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
            pipeline.addLast(new HttpContentCompressor());
            pipeline.addLast(new WebSocketServerProtocolHandler(path, "mqtt,mqttv3.1,mqttv3.1.1", true, 65536));
            pipeline.addLast(new WebSocketMqttCodec());
            pipeline.addLast(new MqttDecoder());
            pipeline.addLast(MqttEncoder.INSTANCE);
            pipeline.addLast(MqttHeartBeatBrokerHandler.INSTANCE);
            return true;
        }
        return false;
    }

    private boolean isMqtt(int magic1, int magic2) {
        return magic1 == 16 && magic2 == 44;
    }
}
