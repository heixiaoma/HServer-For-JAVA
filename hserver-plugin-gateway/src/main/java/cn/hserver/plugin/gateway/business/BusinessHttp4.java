package cn.hserver.plugin.gateway.business;


import cn.hserver.core.ioc.annotation.Bean;
import cn.hserver.plugin.gateway.bean.Http4Data;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.net.SocketAddress;

@Bean
public class BusinessHttp4 implements Business<Http4Data,Object>{

    @Override
    public SocketAddress getProxyHost(ChannelHandlerContext ctx, Http4Data http4Data, SocketAddress sourceSocketAddress) {
        return null;
    }

    @Override
    public Object in(ChannelHandlerContext ctx,Http4Data http4Data) {
        return http4Data.getData();
    }

    @Override
    public Object out(Channel channel, Object o) {
        return o;
    }

    @Override
    public void close(Channel channel) {
    }

    @Override
    public boolean connectController(ChannelHandlerContext ctx,boolean connectResult,int connectNum, Throwable error) {
        return false;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

    }
}
