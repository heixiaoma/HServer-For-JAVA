package top.hserver.cloud.rpc;

import top.hserver.cloud.config.AppRpc;

/**
 * @author hxm
 */
public interface RpcAdapter {
    /**
     * 自定义RPC注册方案
     *
     * @param appRpc
     * @param port
     * @return
     * @throws Exception
     */
    boolean rpcMode(AppRpc appRpc, Integer port) throws Exception;
}
