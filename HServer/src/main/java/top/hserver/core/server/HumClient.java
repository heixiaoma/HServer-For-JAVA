package top.hserver.core.server;

import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.context.HumMessage;
import top.hserver.core.server.util.HumMessageUtil;

import java.net.InetSocketAddress;

public class HumClient {

    public static Channel channel;

    public static void sendMessage(Object data) {
        if (channel != null) {
            channel.writeAndFlush(new DatagramPacket(HumMessageUtil.createMessage(new HumMessage(data)), new InetSocketAddress(
                    "255.255.255.255", ConstConfig.HUM_PORT)));
        }
    }

    public static void sendMessage(Object data, int port) {
        if (channel != null) {
            channel.writeAndFlush(new DatagramPacket(HumMessageUtil.createMessage(new HumMessage(data)), new InetSocketAddress(
                    "255.255.255.255", ConstConfig.HUM_PORT)));
        }
    }

    public static void sendMessage(Object data, String hostName, int port) {
        if (channel != null) {
            channel.writeAndFlush(new DatagramPacket(HumMessageUtil.createMessage(new HumMessage(data)), new InetSocketAddress(
                    hostName, port)));
        }
    }

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
