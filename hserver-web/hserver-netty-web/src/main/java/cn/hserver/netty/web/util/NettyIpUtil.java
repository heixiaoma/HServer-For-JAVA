package cn.hserver.netty.web.util;

import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

public class NettyIpUtil {
    public  static String getClientIp(ChannelHandlerContext ctx) {
        return ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString();
    }
    public  static int getClientPort(ChannelHandlerContext ctx) {
        return ((InetSocketAddress) ctx.channel().remoteAddress()).getPort();
    }
}
