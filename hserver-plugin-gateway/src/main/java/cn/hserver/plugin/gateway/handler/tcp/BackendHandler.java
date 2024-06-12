package cn.hserver.plugin.gateway.handler.tcp;

import cn.hserver.plugin.gateway.business.Business;
import cn.hserver.plugin.gateway.handler.OutBaseChannelInboundHandlerAdapter;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackendHandler extends OutBaseChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(BackendHandler.class);

    public BackendHandler(Channel inboundChannel, Business business) {
        super(inboundChannel, business);
    }
}
