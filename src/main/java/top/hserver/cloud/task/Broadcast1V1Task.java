package top.hserver.cloud.task;

import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.CloudManager;
import top.hserver.cloud.bean.CloudData;
import top.hserver.cloud.client.ChatClient;
import top.hserver.cloud.common.MSG_TYPE;
import top.hserver.cloud.common.Msg;
import top.hserver.cloud.util.NetUtil;
import top.hserver.core.interfaces.TaskJob;
import top.hserver.core.server.util.PropUtil;

/**
 * @author hxm
 */
@Slf4j
public class Broadcast1V1Task implements TaskJob {


    @Override
    public void exec(Object... args) {
        //存在Rpc服务就上报吧,
        if (CloudManager.isRpcService()) {
            if (ChatClient.channel != null && ChatClient.channel.isActive()) {
                //上报服务器
                CloudData cloudData = new CloudData();
                cloudData.setName(args[0].toString());
                cloudData.setClasses(CloudManager.getClasses());
                Msg<CloudData> msg = new Msg<>();
                msg.setMsg_type(MSG_TYPE.REG);
                msg.setData(cloudData);
                ChatClient.channel.writeAndFlush(msg);
            } else {
                try {
                    log.warn("连接被断开。正在重新连接。。。。");
                    if (ChatClient.channel != null) {
                        ChatClient.channel.close();
                    }
                    new ChatClient(args[1].toString(), CloudManager.port).start();
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
    }
}
