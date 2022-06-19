package net.hserver.core.server;

import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
import net.hserver.core.server.context.ConstConfig;
import net.hserver.core.server.context.HumMessage;
import net.hserver.core.server.util.HumMessageUtil;

import java.net.InetSocketAddress;

public class HumClient {

    public static Channel channel;

    public static void sendMessage(HumMessage humMessage, String hostName, int port) {
        if (channel != null) {
            channel.writeAndFlush(new DatagramPacket(HumMessageUtil.createMessage(humMessage), new InetSocketAddress(
                    hostName, port)));
        }
    }

    public static void sendMessage(HumMessage humMessage, int port) {
        if (channel != null) {
            channel.writeAndFlush(new DatagramPacket(HumMessageUtil.createMessage(humMessage), new InetSocketAddress(
                    "255.255.255.255", port)));
        }
    }

    public static void sendMessage(HumMessage humMessage) {
        if (channel != null) {
            channel.writeAndFlush(new DatagramPacket(HumMessageUtil.createMessage(humMessage), new InetSocketAddress(
                    "255.255.255.255", ConstConfig.HUM_PORT)));
        }
    }
}
