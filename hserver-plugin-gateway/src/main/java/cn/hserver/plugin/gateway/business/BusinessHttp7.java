package cn.hserver.plugin.gateway.business;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.net.SocketAddress;

public class BusinessHttp7 implements Business<Object, Object> {


    /**
     * 忽略的url进行消息聚合
     * @return
     */
    public String requestIgnoreUrls() {
        return null;
    }

    public String responseIgnoreUrls() {
        return null;
    }


    @Override
    public Object in(ChannelHandlerContext ctx, Object obj) {
        return obj;
    }

    @Override
    public SocketAddress getProxyHost(ChannelHandlerContext ctx, Object fullHttpRequest, SocketAddress sourceSocketAddress) {
        throw new RuntimeException("请配置需要代理的服务器");
    }

    @Override
    public Object out(Channel channel, Object obj) {
        return obj;
    }

    @Override
    public void close(Channel channel) {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

    }
}
