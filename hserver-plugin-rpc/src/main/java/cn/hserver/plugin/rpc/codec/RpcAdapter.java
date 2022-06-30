package cn.hserver.plugin.rpc.codec;

import cn.hserver.plugin.rpc.bean.RpcServer;

import java.util.List;

/**
 * @author hxm
 */
public interface RpcAdapter {

    /**
     * 自定义RPC注册方案
     *
     * @param rpcServers  开发人员配置的服务
     * @param serverNames 本次项目检查需要的ServerName，更具Resource得来
     */
    void rpcMode(List<RpcServer> rpcServers, List<String> serverNames);
}