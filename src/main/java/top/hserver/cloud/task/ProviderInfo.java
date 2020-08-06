package top.hserver.cloud.task;

import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.CloudManager;
import top.hserver.core.interfaces.TaskJob;

import java.util.List;

import static top.hserver.cloud.CloudManager.naming;

/**
 * @author hxm
 */
@Slf4j
public class ProviderInfo implements TaskJob {

    @Override
    public void exec(Object... args) {
        //注册中心的
        try {
            for (String providerClass : CloudManager.getProviderClass()) {
                List<Instance> allInstances = naming.getAllInstances(providerClass);
                for (Instance allInstance : allInstances) {
                    /**
                     * 服务下线监听待处理
                     */
                    CloudManager.addAddress(allInstance.getIp() + ":" + allInstance.getPort());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
