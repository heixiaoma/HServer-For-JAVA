package top.hserver.cloud.task;

import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.client.RpcClient;
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
                if (RpcClient.channels.get(address) != null && RpcClient.channels.get(address).isActive()) {
                    //上报服务器
                    Msg msg = new Msg<>();
                    msg.setMsg_type(MSG_TYPE.PINGPONG);
                    RpcClient.channels.get(address).writeAndFlush(msg);
                } else {
                    try {
                        log.info("连接提供者，{}", address);
                        if (RpcClient.channels.get(address) != null) {
                            RpcClient.channels.get(address).close();
                            RpcClient.channels.remove(address);
                        }
                        RpcClient.connect(address);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }
            }
        }
    }

}
