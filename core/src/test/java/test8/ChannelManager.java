package test8;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public class ChannelManager {

    private final static AttributeKey<HFuture> FUTURE_ATTRIBUTE_KEY = AttributeKey.valueOf("listener");

    public static HFuture attr(Channel channel) {
        return channel.attr(FUTURE_ATTRIBUTE_KEY).get();
    }

    public static void setAttr(Channel channel, HFuture future) {
        channel.attr(FUTURE_ATTRIBUTE_KEY).set(future);
    }

}