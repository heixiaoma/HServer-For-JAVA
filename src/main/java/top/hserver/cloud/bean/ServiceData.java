package top.hserver.cloud.bean;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

/**
 * @author hxm
 */
@Data
public class ServiceData {

    private ChannelHandlerContext ctx;

    private String name;
}
