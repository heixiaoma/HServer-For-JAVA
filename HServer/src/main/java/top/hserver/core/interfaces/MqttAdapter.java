package top.hserver.core.interfaces;


import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Aop
 *
 * @author hxm
 */
public abstract class MqttAdapter {

    public void connect(MqttMessage mqttMessage, ChannelHandlerContext ctx) {
        MqttFixedHeader connackFixedHeader =
                new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0);
        MqttConnAckVariableHeader mqttConnAckVariableHeader =
                new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, false);
        MqttConnAckMessage connack = new MqttConnAckMessage(connackFixedHeader, mqttConnAckVariableHeader);
        ctx.writeAndFlush(connack);
    }


    public void pingReq(MqttMessage mqttMessage, ChannelHandlerContext ctx) {
        MqttFixedHeader pingreqFixedHeader = new MqttFixedHeader(MqttMessageType.PINGRESP, false,
                MqttQoS.AT_MOST_ONCE, false, 0);
        MqttMessage pingResp = new MqttMessage(pingreqFixedHeader);
        ctx.writeAndFlush(pingResp);
    }

    public void disconnect(MqttMessage mqttMessage, ChannelHandlerContext ctx) {
        ctx.close();
    }

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent && IdleState.READER_IDLE == ((IdleStateEvent) evt).state()) {
            ctx.close();
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 消息处理
     */
    public abstract void message(MqttMessageType type, MqttMessage mqttMessage, ChannelHandlerContext ctx);

}