package cn.hserver.plugin.gateway.business;


import cn.hserver.core.ioc.annotation.Bean;
import cn.hserver.plugin.gateway.bean.Http4Data;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.net.SocketAddress;

@Bean
public class BusinessTcp implements Business<Object,Object>{

    @Override
    public Object in(ChannelHandlerContext ctx, Object o) {
        return o;
    }

    @Override
    public SocketAddress getProxyHost(ChannelHandlerContext ctx,Object o, SocketAddress sourceSocketAddress) {
        return null;
    }

    @Override
    public Object out(Channel channel, Object o) {
        return o;
    }
}
