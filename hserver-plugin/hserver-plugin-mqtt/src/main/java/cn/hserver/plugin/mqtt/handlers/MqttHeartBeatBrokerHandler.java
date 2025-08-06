package cn.hserver.plugin.mqtt.handlers;

import cn.hserver.HServerApplication;
import cn.hserver.plugin.mqtt.interfaces.MqttAdapter;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;
import cn.hserver.core.ioc.IocUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hxm
 */
@Sharable
public final class MqttHeartBeatBrokerHandler extends SimpleChannelInboundHandler<MqttMessage> {
    private static final Logger log = LoggerFactory.getLogger(MqttHeartBeatBrokerHandler.class);

    public static final MqttHeartBeatBrokerHandler INSTANCE = new MqttHeartBeatBrokerHandler();
    private final MqttAdapter mqttAdapter = IocUtil.getSupperBean(MqttAdapter.class);

    private MqttHeartBeatBrokerHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        if (mqttAdapter == null) {
            log.error("请继承MqttAdapter类 并用@Bean 标记");
        } else {
            mqttAdapter.channelActive( ctx);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        if (mqttAdapter == null) {
            log.error("请继承MqttAdapter类 并用@Bean 标记");
        } else {
            mqttAdapter.channelInactive( ctx);
        }
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage mqttMessage) throws Exception {
        if (mqttAdapter == null) {
            log.error("请继承MqttAdapter类 并用@Bean 标记");
        } else {
            switch (mqttMessage.fixedHeader().messageType()) {
                case CONNECT:
                    mqttAdapter.connect(mqttMessage, ctx);
                    break;
                case PINGREQ:
                    mqttAdapter.pingReq(mqttMessage, ctx);
                    break;
                case DISCONNECT:
                    mqttAdapter.disconnect(mqttMessage, ctx);
                    break;
                default:
                    mqttAdapter.message(mqttMessage.fixedHeader().messageType(), mqttMessage, ctx);
            }
        }

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (mqttAdapter == null) {
            log.error("请继承MqttAdapter类 并用@Bean 标记");
        } else {
            mqttAdapter.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (mqttAdapter == null) {
            log.error("请继承MqttAdapter类 并用@Bean 标记");
        } else {
            mqttAdapter.exceptionCaught(ctx, cause);
        }
    }
}
