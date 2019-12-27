package top.hserver.cloud.task;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.bean.CloudData;
import top.hserver.cloud.client.handler.RegClient;
import top.hserver.cloud.util.NetUtil;
import top.hserver.core.interfaces.TaskJob;

@Slf4j
public class BroadcastTask implements TaskJob {

    @Override
    public void exec(Object... args) {
        //上报服务器
        CloudData cloudData = new CloudData();
        cloudData.setIp(NetUtil.getIpAddress());
        cloudData.setName("ServerA");
        String s = JSON.toJSONString(cloudData);
        log.info(s);
        RegClient.Send(s);
    }
}
