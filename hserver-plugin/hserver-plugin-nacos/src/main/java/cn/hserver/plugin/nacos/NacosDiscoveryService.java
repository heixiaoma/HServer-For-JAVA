package cn.hserver.plugin.nacos;

import cn.hserver.cloud.DiscoveryHandler;
import cn.hserver.cloud.DiscoveryService;
import cn.hserver.cloud.RegProp;
import cn.hserver.cloud.ServerInstance;
import cn.hserver.core.ioc.annotation.Bean;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Bean(DiscoveryService.DISCOVERY_SERVICE)
public class NacosDiscoveryService implements DiscoveryService {

    private static final Logger log = LoggerFactory.getLogger(NacosDiscoveryService.class);

    private NamingService naming;

    private RegProp regProp;


    @Override
    public boolean register(RegProp regProp) {
        this.regProp = regProp;
        try {
            naming = NamingFactory.createNamingService(regProp.getRegisterAddress());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        try {
            //注册服务
            naming.registerInstance(
                    regProp.getRegisterName(),
                    regProp.getGroupName(),
                    regProp.getRegisterMyIp(),
                    regProp.getRegisterMyPort(),
                    regProp.getRegisterName()
            );
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return false;
        }
        return true;
    }

    @Override
    public boolean deregister() {
        try {
            naming.deregisterInstance(regProp.getRegisterName(), regProp.getGroupName(), regProp.getRegisterMyIp(), regProp.getRegisterMyPort());
        } catch (NacosException e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public List<ServerInstance> find(String group, String service) {
        if (group == null) {
            group = Constants.DEFAULT_GROUP;
        }
        try {
            final List<Instance> instances = naming.selectInstances(service, group, true);
            return instances.stream().map(k -> new ServerInstance(
                            k.getIp(),
                            k.getPort(),
                            k.getWeight(),
                            k.isHealthy(),
                            k.getServiceName(),
                            k.getClusterName(),
                            k.getMetadata()
                    )
            ).collect(Collectors.toList());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void subscribe(String group, String service, DiscoveryHandler discoveryHandler) {
        if (group == null) {
            group = Constants.DEFAULT_GROUP;
        }
        try {
            String finalGroup = group;
            EventListener listener = event -> {
                if (event instanceof NamingEvent) {
                    NamingEvent evn = (NamingEvent) event;
                    List<Instance> instances = evn.getInstances();
                    log.debug("服务变化：" + instances);

                    Map<String, List<ServerInstance>> data = new HashMap<>();
                    for (Instance k : instances) {
                        List<ServerInstance> serverInstances = data.computeIfAbsent(k.getClusterName(), k1 -> new ArrayList<>());
                        serverInstances.add(new ServerInstance(
                                k.getIp(),
                                k.getPort(),
                                k.getWeight(),
                                k.isHealthy(),
                                k.getServiceName(),
                                k.getClusterName(),
                                k.getMetadata()
                        ));
                    }
                    discoveryHandler.handler(finalGroup,data);
                }
            };
            naming.subscribe(service, group, listener);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
