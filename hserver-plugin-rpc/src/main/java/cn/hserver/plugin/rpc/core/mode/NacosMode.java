package cn.hserver.plugin.rpc.core.mode;

import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import cn.hserver.plugin.rpc.bean.RpcServer;
import cn.hserver.plugin.rpc.client.RpcClient;
import cn.hserver.plugin.rpc.codec.RpcAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NacosMode implements RpcAdapter {

    private static final Logger log = LoggerFactory.getLogger(NacosMode.class);

    //注册中心地址
    private String registerAddress;
    //注册名字
    private String registerName;
    //注册我的Ip
    private String registerMyIp;
    //注册我的端口
    private Integer registerMyPort;

    private String groupName = Constants.DEFAULT_GROUP;

    public void setRegisterAddress(String registerAddress) {
        this.registerAddress = registerAddress;
    }

    public void setRegisterName(String registerName) {
        this.registerName = registerName;
    }

    public void setRegisterMyIp(String registerMyIp) {
        this.registerMyIp = registerMyIp;
    }

    public void setRegisterMyPort(Integer registerMyPort) {
        this.registerMyPort = registerMyPort;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public void rpcMode(List<RpcServer> rpcServers, List<String> serverNames) {
        if (this.registerAddress == null) {
            throw new NullPointerException("Nacos注册地址不能为空");
        }
        if (this.registerName == null) {
            throw new NullPointerException("Nacos注册的名字不能为空");
        }
        if (this.registerMyIp == null) {
            throw new NullPointerException("Nacos注册的自己的IP不能为空");
        }
        if (this.registerMyPort == null) {
            throw new NullPointerException("Nacos注册的自己的Port不能为空");
        }
        try {
            /**
             * nacos 客服端
             */
            NamingService naming = NamingFactory.createNamingService(this.registerAddress);
            naming.registerInstance(this.registerName, this.groupName, this.registerMyIp, this.registerMyPort, this.registerName);

            /**
             * 订阅注册的数据
             */
            subProviderInfo(naming, rpcServers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void subProviderInfo(NamingService naming, List<RpcServer> rpcServers) {
        /**
         *        按需订阅属于自己的需要的服务
         *
         */
        rpcServers.forEach(regServerName -> {
            try {
                EventListener listener = event -> {
                    if (event instanceof NamingEvent) {
                        NamingEvent evn = (NamingEvent) event;
                        List<Instance> instances = evn.getInstances();
                        log.info("服务变化：" + instances);
                        //节点变化，主动对上下线关系进行清除，重新设置
                        RpcClient.remove(regServerName);
                        for (Instance instance : instances) {
                            RpcServer rpcServer = new RpcServer();
                            rpcServer.setServerName(regServerName.getServerName());
                            rpcServer.setPort(instance.getPort());
                            rpcServer.setIp(instance.getIp());
                            //新变化的节点加入服务
                            RpcClient.reg(rpcServer);
                        }
                    }
                };
                naming.subscribe(regServerName.getServerName(), listener);
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        });
    }

}