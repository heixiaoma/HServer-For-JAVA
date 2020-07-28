package top.hserver.cloud.task;

import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.CloudManager;
import top.hserver.cloud.bean.CloudData;
import top.hserver.cloud.client.ChatClient;
import top.hserver.cloud.common.MSG_TYPE;
import top.hserver.cloud.common.Msg;
import top.hserver.core.interfaces.TaskJob;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hxm
 */
@Slf4j
public class BroadcastNacosTask implements TaskJob {

    private NamingService naming;

    @Override
    public void exec(Object... args) {
        //存在Rpc服务就上报吧,
        String rpcServerName = args[0].toString();
        //注册中心的
        String host = args[1].toString();
        String post = args[2].toString();
        String host1 = args[3].toString();
        String post2 = args[4].toString();

        if (naming == null) {
            try {
                naming = NamingFactory.createNamingService(host+":"+post);
                if (CloudManager.isRpcService()) {
                    //上报服务器
                    Instance instance = new Instance();
                    instance.setIp(host1);
                    instance.setPort(Integer.parseInt(post2));
                    instance.setHealthy(true);
                    instance.setWeight(0);
                    Map<String,String> data=new HashMap<>(1);
                    data.put("key",CloudManager.getClasses().toString());
                    instance.setMetadata(data);
                    naming.registerInstance(rpcServerName, instance);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Nacos 注册中心注册失败");
                naming = null;
            }

        }
    }
}
