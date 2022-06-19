package cn.hserver.plugin.rpc.core.mode;

import cn.hserver.plugin.rpc.bean.RpcServer;
import cn.hserver.plugin.rpc.client.RpcClient;
import cn.hserver.plugin.rpc.codec.RpcAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DefaultMode implements RpcAdapter {

    private static final Logger log = LoggerFactory.getLogger(DefaultMode.class);

    @Override
    public void rpcMode(List<RpcServer> rpcServers, List<String> serverNames) {
        for (RpcServer rpcServer : rpcServers) {
            if (serverNames.contains(rpcServer.getServerName())) {
                RpcClient.reg(rpcServer);
            }else {
                log.warn("{} 服务没用上建议不配置", rpcServer.getServerName());
            }
        }
    }
}