package cn.hserver.plugin.gateway.business;


import cn.hserver.core.ioc.annotation.Bean;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.net.SocketAddress;

@Bean
public class BusinessHttp7 implements Business<Object, Object>{

    @Override
    public Object in(ChannelHandlerContext ctx, Object obj) {
        return obj;
    }

    @Override
    public SocketAddress getProxyHost(ChannelHandlerContext ctx,Object fullHttpRequest, SocketAddress sourceSocketAddress) {
        return null;
    }

    @Override
    public Object out(Channel channel, Object obj) {
        return obj;
    }

    @Override
    public void close(Channel channel) {

    }
}
