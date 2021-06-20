package top.hserver.cloud;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hserver.cloud.client.RpcClient;
import top.hserver.cloud.config.AppRpc;
import top.hserver.cloud.proxy.CloudProxy;
import top.hserver.cloud.rpc.RpcAdapter;
import top.hserver.core.ioc.IocUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author hxm
 */
public class CloudManager {
    private static final Logger log = LoggerFactory.getLogger(CloudManager.class);
    private static Set<String> ServerNames = new CopyOnWriteArraySet<>();
    /**
     * 初始化rpc
     *
     * @param port
     */
    public static void run(Integer port) {
        //清除启动的Map缓存
        CloudProxy.clearCache();
        try {
            RpcClient.init();
            AppRpc appRpc = IocUtil.getBean(AppRpc.class);
            if (appRpc != null) {
                List<RpcAdapter> listBean = IocUtil.getListBean(RpcAdapter.class);
                if (listBean!=null) {
                    for (RpcAdapter rpcAdapter : listBean) {
                        if (rpcAdapter.rpcMode(appRpc, port)) {
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }


    /**
     * 消费者涉及，通过服务名去订阅Nacos
     *
     * @param name
     */
    public static void add(String name) {
        ServerNames.add(name);
    }

    /**
     * 获取服务名
     *
     * @return
     */
    public static List<String> getServerNames() {
        return new ArrayList<>(ServerNames);
    }
}
