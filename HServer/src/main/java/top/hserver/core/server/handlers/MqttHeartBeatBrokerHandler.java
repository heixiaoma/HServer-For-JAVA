package top.hserver.core.server.handlers;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import top.hserver.core.interfaces.MqttAdapter;
import top.hserver.core.ioc.IocUtil;

/**
 * @author hxm
 */
@Sharable
public final class MqttHeartBeatBrokerHandler extends ChannelInboundHandlerAdapter {

    public static final MqttHeartBeatBrokerHandler INSTANCE = new MqttHeartBeatBrokerHandler();

    private MqttHeartBeatBrokerHandler() {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            MqttAdapter bean = IocUtil.getBean(MqttAdapter.class);
            if (bean == null) {
                System.out.println("请继承MqttAdapter类 并用@Bean 标记");
            } else {
                MqttMessage mqttMessage = (MqttMessage) msg;
                switch (mqttMessage.fixedHeader().messageType()) {
                    case CONNECT:
                        bean.connect(mqttMessage, ctx);
                        break;
                    case PINGREQ:
                        bean.pingReq(mqttMessage, ctx);
                        break;
                    case DISCONNECT:
                        bean.disconnect(mqttMessage, ctx);
                        break;
                    default:
                        bean.message(mqttMessage.fixedHeader().messageType(), mqttMessage, ctx);
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        MqttAdapter bean = IocUtil.getBean(MqttAdapter.class);
        if (bean == null) {
            System.out.println("请继承MqttAdapter类 并用@Bean 标记");
        } else {
            bean.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        MqttAdapter bean = IocUtil.getBean(MqttAdapter.class);
        if (bean == null) {
            System.out.println("请继承MqttAdapter类 并用@Bean 标记");
        } else {
            exceptionCaught(ctx, cause);
        }
    }
}