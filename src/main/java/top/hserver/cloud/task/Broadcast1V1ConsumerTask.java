package top.hserver.cloud.task;

import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.client.ChatClient;
import top.hserver.cloud.common.MSG_TYPE;
import top.hserver.cloud.common.Msg;
import top.hserver.core.interfaces.TaskJob;

/**
 * @author hxm
 */
@Slf4j
public class Broadcast1V1ConsumerTask implements TaskJob {


    @Override
    public void exec(Object... args) {
        String addresss = args[0] == null ? null : args[0].toString();
        String[] split = addresss.split(",");
        for (String address : split) {
            if (address != null) {
                if (ChatClient.channels.get(address) != null && ChatClient.channels.get(address).isActive()) {
                    //上报服务器
                    Msg msg = new Msg<>();
                    msg.setMsg_type(MSG_TYPE.PINGPONG);
                    ChatClient.channels.get(address).writeAndFlush(msg);
                } else {
                    try {
                        log.warn("连接提供者，{}", address);
                        if (ChatClient.channels.get(address) != null) {
                            ChatClient.channels.get(address).close();
                        }
                        new ChatClient(address).start();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }
            }
        }
    }

}
