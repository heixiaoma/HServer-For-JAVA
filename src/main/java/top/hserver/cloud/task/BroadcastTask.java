package top.hserver.cloud.task;

import top.hserver.cloud.client.handler.RegClient;
import top.hserver.core.interfaces.TaskJob;

public class BroadcastTask implements TaskJob {

    @Override
    public void exec(Object... args) {
        //上报服务器
        RegClient.Send(args[0]+"：服务注册事件");
    }
}
