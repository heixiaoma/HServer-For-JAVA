package cn.hserver.plugin.gateway.business;


import cn.hserver.core.ioc.annotation.Bean;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.net.SocketAddress;

public class BusinessTcp implements Business<Object,Object>{

    @Override
    public Object in(ChannelHandlerContext ctx, Object o) {
        return o;
    }

    @Override
    public SocketAddress getProxyHost(ChannelHandlerContext ctx,Object o, SocketAddress sourceSocketAddress) {
        throw new RuntimeException("请配置需要代理的服务器");
    }

    @Override
    public Object out(Channel channel, Object o) {
        return o;
    }

    @Override
    public void close(Channel channel) {

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

    }
}
