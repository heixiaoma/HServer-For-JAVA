package net.hserver.core.server.util;

import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

/**
 * @author hxm
 */
public class HServerIpUtil {

    public  static String getClientIp(ChannelHandlerContext ctx) {
        return ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString();
    }
    public  static int getClientPort(ChannelHandlerContext ctx) {
        return ((InetSocketAddress) ctx.channel().remoteAddress()).getPort();
    }
}
