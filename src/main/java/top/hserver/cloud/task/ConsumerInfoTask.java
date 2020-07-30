package top.hserver.cloud.task;

import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import top.hserver.core.interfaces.TaskJob;

import java.util.List;

/**
 * @author hxm
 */
@Slf4j
public class ConsumerInfoTask implements TaskJob {

    private NamingService naming;

    @Override
    public void exec(Object... args) {
        //注册中心的
        String host = args[0].toString();
        String post = args[1].toString();
        String serverNames = args[2].toString();
            try {
                if (naming == null) {
                    naming = NamingFactory.createNamingService(host + ":" + post);
                }
                for (String s : serverNames.split(",")) {
                    List<Instance> allInstances = naming.getAllInstances(s);
                    System.out.println(allInstances);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Nacos 注册中心获取失败");
                naming = null;
            }
        }

}
