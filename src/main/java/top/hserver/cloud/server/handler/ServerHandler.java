package top.hserver.cloud.server.handler;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.bean.CloudData;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static Set<CloudData> cloudDataS=new CopyOnWriteArraySet<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        ByteBuf buf = packet.copy().content();
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8");
        CloudData cloudData = JSON.parseObject(body, CloudData.class);
        cloudDataS.add(cloudData);
        log.info("服务器端实例："+cloudDataS.size());
    }

}