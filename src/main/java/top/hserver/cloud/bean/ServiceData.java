package top.hserver.cloud.bean;

import com.alibaba.nacos.api.naming.pojo.Instance;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

/**
 * @author hxm
 */
@Data
public class ServiceData {

    private Channel channel;

    private String name;

    /**
     * 针对Nacos才会使用
     */
    private Instance instance;
}
