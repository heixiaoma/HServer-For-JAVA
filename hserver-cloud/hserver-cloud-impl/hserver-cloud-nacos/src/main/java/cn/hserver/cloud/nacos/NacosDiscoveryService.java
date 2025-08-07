package cn.hserver.cloud.nacos;

import cn.hserver.cloud.common.DisProp;
import cn.hserver.cloud.common.ServerInstance;
import cn.hserver.cloud.discovery.DiscoveryListener;
import cn.hserver.cloud.discovery.DiscoveryService;
import cn.hserver.core.config.ConfigData;
import cn.hserver.core.context.IocApplicationContext;
import cn.hserver.core.ioc.annotation.Component;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class NacosDiscoveryService extends DiscoveryService{

    private static final Logger log = LoggerFactory.getLogger(NacosDiscoveryService.class);

    private  NamingService naming;

    public  NacosDiscoveryService() {
        DisProp disProp = IocApplicationContext.getBean(DisProp.class);
        if (disProp==null) {
            String string = ConfigData.getInstance().getString("nacos.discovery.address",null);
            disProp=new DisProp(string);
        }
        try {
            naming = NamingFactory.createNamingService(disProp.getDiscoveryAddress());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void subscribe(String group, String service, DiscoveryListener discoveryListener) {
        if (group == null) {
            group = Constants.DEFAULT_GROUP;
        }
        try {
            String finalGroup = group;
            EventListener listener = event -> {
                if (event instanceof NamingEvent) {
                    NamingEvent evn = (NamingEvent) event;
                    List<Instance> instances = evn.getInstances();
                    log.debug("服务变化{}" , instances);
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
                    discoveryListener.onChanged(finalGroup,data);
                }
            };
            naming.subscribe(service, group, listener);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    @Override
    public ServerInstance findOne(String group, String service) {
        if (group == null) {
            group = Constants.DEFAULT_GROUP;
        }
        try {
            Instance instance = naming.selectOneHealthyInstance(service, group, true);
            return new ServerInstance(
                    instance.getIp(),
                    instance.getPort(),
                    instance.getWeight(),
                    instance.isHealthy(),
                    instance.getServiceName(),
                    instance.getClusterName(),
                    instance.getMetadata()
            );
        }catch (Exception e) {
            return null;
        }
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

}
