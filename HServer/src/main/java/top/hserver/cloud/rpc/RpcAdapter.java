package top.hserver.cloud.rpc;

import top.hserver.cloud.config.AppRpc;

import java.util.List;

/**
 * @author hxm
 */
public interface RpcAdapter {

    /**
     * 自定义RPC注册方案
     *
     * @param appRpc
     * @param port
     * @param serverNames 本次服务需要的ServerName
     * @return
     * @throws Exception
     */
    boolean rpcMode(AppRpc appRpc, Integer port, List<String> serverNames) throws Exception;
}
