package top.hserver.cloud.task;

import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.CloudManager;
import top.hserver.cloud.client.ChatClient;
import top.hserver.cloud.common.MSG_TYPE;
import top.hserver.cloud.common.Msg;
import top.hserver.core.interfaces.TaskJob;

import java.util.Set;

/**
 * @author hxm
 */
@Slf4j
public class KeepLiveTask implements TaskJob {

    @Override
    public void exec(Object... args) {
        for (String address : CloudManager.getAddress()) {
            if (ChatClient.channels.get(address) != null && ChatClient.channels.get(address).isActive()) {
                //上报服务器
                Msg msg = new Msg<>();
                msg.setMsg_type(MSG_TYPE.PINGPONG);
                ChatClient.channels.get(address).writeAndFlush(msg);
            } else {
                try {
                    log.info("连接提供者，{}", address);
                    if (ChatClient.channels.get(address) != null) {
                        ChatClient.channels.get(address).close();
                        ChatClient.channels.remove(address);
                    }
                    new ChatClient(address).start();
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
    }
}
