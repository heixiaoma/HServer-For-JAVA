package top.hserver.cloud.task;

import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hserver.cloud.CloudManager;
import top.hserver.cloud.client.handler.RpcClientHandler;

import java.util.List;

import static top.hserver.cloud.CloudManager.naming;

/**
 * @author hxm
 */
public class SubProviderInfo {
    private static final Logger log = LoggerFactory.getLogger(SubProviderInfo.class);

    public static void init() {
        //注册中心的
        CloudManager.getServerNames().forEach(p -> {
            try {
                EventListener listener = event -> {
                    if (event instanceof NamingEvent) {
                        NamingEvent evn = (NamingEvent) event;
                        List<Instance> instances = evn.getInstances();
                        //节点变化，对上下线关系进行清除，重新设置
                        RpcClientHandler.nacosClear(p);
                        for (Instance instance : instances) {
                            RpcClientHandler.nacosReg(instance.getIp(), instance.getPort(), p);
                        }
                    }
                };
                naming.subscribe(p, listener);
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        });
    }
}
