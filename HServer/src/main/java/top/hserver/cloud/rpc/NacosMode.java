package top.hserver.cloud.rpc;

import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hserver.cloud.bean.ServiceData;
import top.hserver.cloud.client.handler.RpcClientHandler;
import top.hserver.cloud.config.AppRpc;
import top.hserver.cloud.config.AppRpcNacos;
import top.hserver.core.ioc.IocUtil;
import top.hserver.core.ioc.annotation.Bean;

import java.util.List;


/**
 * @author hxm
 */
@Bean
public class NacosMode implements RpcAdapter {

    private static final Logger log = LoggerFactory.getLogger(NacosMode.class);

    /**
     * Nacos注册中心
     */
    private NamingService naming;

    @Override
    public boolean rpcMode(AppRpc appRpc, Integer port) throws NacosException {
        if (appRpc.getMode() != null && "nacos".equalsIgnoreCase(appRpc.getMode())) {
            AppRpcNacos appRpcNacos = IocUtil.getBean(AppRpcNacos.class);
            if (appRpcNacos == null || appRpcNacos.getAddress() == null) {
                throw new NullPointerException("Nacos注册中心不能为空");
            }
            if (appRpcNacos.getName() == null) {
                throw new NullPointerException("app.rpc.naocs.name不能为空");
            }
            if (appRpcNacos.getIp() == null) {
                throw new NullPointerException("Nacos模式，自己的IP不能为空");
            }
            /**
             * nacos 客服端
             */
            naming = NamingFactory.createNamingService(appRpcNacos.getAddress());
            /**
             * 不论是消费者还生产者都要去注册中心注册
             */
            if (appRpcNacos.getGroup() == null || appRpcNacos.getGroup().trim().length() == 0) {
                appRpcNacos.setGroup(Constants.DEFAULT_GROUP);
            }
            naming.registerInstance(appRpcNacos.getName(), appRpcNacos.getGroup(), appRpcNacos.getIp(), port, appRpcNacos.getName());
            /**
             * 订阅注册的数据
             */
            subProviderInfo(appRpcNacos.getName());
            return true;
        }
        return false;
    }

    private ServiceData nacosReg(String host, Integer port, String serverName) {
        ServiceData serviceData = new ServiceData();
        serviceData.setHost(host);
        serviceData.setPort(port);
        serviceData.setServerName(serverName);
        return serviceData;
    }

    private void subProviderInfo(String regName) {
        //注册中心的
        try {
            EventListener listener = event -> {
                if (event instanceof NamingEvent) {
                    NamingEvent evn = (NamingEvent) event;
                    List<Instance> instances = evn.getInstances();
                    //节点变化，主动对上下线关系进行清除，重新设置
                    RpcClientHandler.clear(regName);
                    for (Instance instance : instances) {
                        RpcClientHandler.reg(nacosReg(instance.getIp(), instance.getPort(), regName));
                    }
                }
            };
            naming.subscribe(regName, listener);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }
}
