package cn.hserver.plugin.rpc.config;

import cn.hserver.plugin.rpc.bean.RpcServer;
import cn.hserver.plugin.rpc.codec.RpcAdapter;

import java.util.ArrayList;
import java.util.List;

public class RpcConfig {

    //最大连接数
    private int maxIdle = 5;
    //最小连接数
    private int minIdle = 1;
    //总共连接数
    private int maxTotal = 10;

    private List<RpcServer> rpcServers = new ArrayList<>();

    RpcAdapter rpcAdapter;

    public void addRpcServer(RpcServer rpcServer) {
        rpcServers.add(rpcServer);
    }

    public RpcAdapter getRpcAdapter() {
        return rpcAdapter;
    }

    public void setRpcAdapter(RpcAdapter rpcAdapter) {
        this.rpcAdapter = rpcAdapter;
    }

    public List<RpcServer> getRpcServers() {
        return rpcServers;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }
}
