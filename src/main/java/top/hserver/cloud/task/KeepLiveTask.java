package top.hserver.cloud.task;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.CloudManager;
import top.hserver.cloud.bean.ServiceData;
import top.hserver.cloud.client.NacosRpcClient;
import top.hserver.cloud.client.RpcClient;
import top.hserver.cloud.client.handler.RpcServerHandler;
import top.hserver.cloud.common.MSG_TYPE;
import top.hserver.cloud.common.Msg;
import top.hserver.cloud.util.DynamicRoundRobin;
import top.hserver.core.interfaces.TaskJob;

import java.util.List;
import java.util.Map;

/**
 * @author hxm
 */
@Slf4j
public class KeepLiveTask implements TaskJob {

    @Override
    public void exec(Object... args) {
        Map<String, DynamicRoundRobin<ServiceData>> classStringMap = RpcServerHandler.CLASS_STRING_MAP;
        classStringMap.forEach((k, v) -> {
            List<ServiceData> serviceDataList = v.getAll();
            for (int i = 0; i < serviceDataList.size(); i++) {
                ServiceData serviceData = serviceDataList.get(i);
                Channel channel = serviceData.getChannel();
                if (channel != null && channel.isActive()) {
                    //ping-pong
                    Msg msg = new Msg<>();
                    msg.setMsg_type(MSG_TYPE.PINGPONG);
                    channel.writeAndFlush(msg);
                } else {
                    try {
                        log.warn("channel 异常-重连中，{}", serviceData.getName());
                        if (channel != null) {
                            channel.close();
                        }
                        v.remove(serviceData);
                        NacosRpcClient.reconnect(serviceData, k);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }
            }
        });

    }
}
