package cn.hserver.core.server.handlers;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import cn.hserver.core.interfaces.MqttAdapter;
import cn.hserver.core.ioc.IocUtil;

/**
 * @author hxm
 */
@Sharable
public final class MqttHeartBeatBrokerHandler extends SimpleChannelInboundHandler<MqttMessage> {

    public static final MqttHeartBeatBrokerHandler INSTANCE = new MqttHeartBeatBrokerHandler();

    private MqttHeartBeatBrokerHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        MqttAdapter bean = IocUtil.getBean(MqttAdapter.class);
        if (bean == null) {
            System.out.println("请继承MqttAdapter类 并用@Bean 标记");
        } else {
            bean.channelActive( ctx);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        MqttAdapter bean = IocUtil.getBean(MqttAdapter.class);
        if (bean == null) {
            System.out.println("请继承MqttAdapter类 并用@Bean 标记");
        } else {
            bean.channelInactive( ctx);
        }
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage mqttMessage) throws Exception {
        MqttAdapter bean = IocUtil.getBean(MqttAdapter.class);
        if (bean == null) {
            System.out.println("请继承MqttAdapter类 并用@Bean 标记");
        } else {
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
            bean.exceptionCaught(ctx, cause);
        }
    }
}