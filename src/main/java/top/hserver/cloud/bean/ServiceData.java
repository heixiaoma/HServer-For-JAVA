package top.hserver.cloud.bean;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

@Data
public class ServiceData {
    private ChannelHandlerContext ctx;
    private String name;
    private String ip;
}
