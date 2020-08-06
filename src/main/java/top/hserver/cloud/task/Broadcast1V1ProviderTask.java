package top.hserver.cloud.task;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.CloudManager;
import top.hserver.cloud.bean.RegRpcData;
import top.hserver.cloud.common.MSG_TYPE;
import top.hserver.cloud.common.Msg;
import top.hserver.cloud.server.handler.InvokerHandler;
import top.hserver.core.interfaces.TaskJob;

import java.util.Iterator;

/**
 * @author hxm
 */
@Slf4j
public class Broadcast1V1ProviderTask implements TaskJob {


    /**
     * 服务提供者上报消费者
     * @param args
     */
    @Override
    public void exec(Object... args) {
        if (CloudManager.isRpcService()) {
            Iterator<Channel> it = InvokerHandler.consumerChannel.iterator();
            while (it.hasNext()) {
                Channel channel = it.next();
                if (channel.isActive()) {
                    //上报服务器
                    RegRpcData cloudData = new RegRpcData();
                    cloudData.setName(args[0].toString());
                    cloudData.setClasses(CloudManager.getClasses());
                    Msg<RegRpcData> msg = new Msg<>();
                    msg.setMsg_type(MSG_TYPE.REG);
                    msg.setData(cloudData);
                    channel.writeAndFlush(msg);
                } else {
                    it.remove();
                }
            }
        }
    }
}
