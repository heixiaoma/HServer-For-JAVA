package cn.hserver.plugin.rpc.config;


public class RpcConfig {

    //最大连接数
    private int maxIdle = 5;
    //最小连接数
    private int minIdle = 1;
    //总共连接数
    private int maxTotal = 10;

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
