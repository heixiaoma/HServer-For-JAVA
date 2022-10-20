package cn.hserver.plugin.gateway.business;


import cn.hserver.core.ioc.annotation.Bean;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObject;

import java.net.SocketAddress;

@Bean
public class BusinessHttp7 implements Business<FullHttpRequest, Object>{

    @Override
    public Object in(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
        return fullHttpRequest;
    }

    @Override
    public SocketAddress getProxyHost(ChannelHandlerContext ctx,FullHttpRequest fullHttpRequest, SocketAddress sourceSocketAddress) {
        return null;
    }

    @Override
    public Object out(Channel channel, Object obj) {
        return obj;
    }
}
